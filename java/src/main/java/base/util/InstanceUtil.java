package base.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jsonwebtoken.lang.Arrays;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import sun.misc.Unsafe;

public class InstanceUtil {
	private static final Map<Class<?>, Class<?>> WRAPPER_TYPE_MAP;
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	static {
		WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>(16);
		WRAPPER_TYPE_MAP.put(Integer.class, int.class);
		WRAPPER_TYPE_MAP.put(Byte.class, byte.class);
		WRAPPER_TYPE_MAP.put(Character.class, char.class);
		WRAPPER_TYPE_MAP.put(Boolean.class, boolean.class);
		WRAPPER_TYPE_MAP.put(Double.class, double.class);
		WRAPPER_TYPE_MAP.put(Float.class, float.class);
		WRAPPER_TYPE_MAP.put(Long.class, long.class);
		WRAPPER_TYPE_MAP.put(Short.class, short.class);
		WRAPPER_TYPE_MAP.put(Void.class, void.class);
		WRAPPER_TYPE_MAP.put(String.class, String.class);

	}

	public static boolean isPrimitive(Class<?> source) {

		var isPrimitive = source.isEnum() || Temporal.class.isAssignableFrom(source)
				|| Date.class.isAssignableFrom(source) || Number.class.isAssignableFrom(source);

		return isPrimitive || WRAPPER_TYPE_MAP.containsKey(source);
	}

	public static <T, J extends T> void copyTo(T source, J destination, String... fieldsIgnoraveis) {

		try {
			copyTo(source, source.getClass(), destination, Arrays.asList(fieldsIgnoraveis));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Field> getAllFields(Class c) {
		List<Field> l = new ArrayList();
		l.addAll(Arrays.asList(c.getDeclaredFields()));

		if (Objects.nonNull(c.getSuperclass())) {
			List<Field> superFields = getAllFields(c.getSuperclass());
			l.addAll(superFields.stream().filter(f -> !l.stream().anyMatch(f2 -> f2.getName().equals(f.getName()))

			).toList());
		}

		return l;
	}

	private static void copyList(Object sourceValue, Object target, Field targetField, Map<Object, Object> instances)
			throws IllegalArgumentException, IllegalAccessException, SecurityException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		Object value = null;

		if (targetField.getType().isAssignableFrom(List.class)) {
			List list = new ArrayList();
			CopyItem((Iterable) sourceValue,
					(Class) ((ParameterizedType) targetField.getGenericType()).getActualTypeArguments()[0],
					(item, index) -> {
						list.add(item);

					}, instances);

			value = list;
		} else if (targetField.getType().isArray()) {
			long sizeList = StreamSupport.stream(((Iterable<Object>) sourceValue).spliterator(), false).count();

			Object array = Array.newInstance(targetField.getType().getClass(), (int) sizeList);

			CopyItem((Iterable) sourceValue, targetField.getType().getClass(), (item, index) -> {
				Array.set(array, index, item);

			}, instances);

			value = array;
		}

		targetField.set(target, value);
	}

	private static void CopyItem(Iterable source, Class<?> itemType, Loop<Object> func, Map<Object, Object> instances)
			throws IllegalArgumentException, IllegalAccessException, SecurityException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		int index = 0;
		for (var item : source) {

			Object newItem = instances.get(item);

			if (Objects.isNull(newItem)) {

				newItem = itemType.getConstructor().newInstance();
				instances.put(item, newItem);

				copyToDeep(item, newItem, instances);
			}

			func.execute(newItem, index++);
		}
	}

	private static <S, D> void copyToDeep2(S source, Class sourceClass, D destination, Class destinationClass,
			Map<Object, Object> instances) throws IllegalArgumentException, IllegalAccessException, SecurityException,
			InstantiationException, InvocationTargetException, NoSuchMethodException {
		List<Field> souceFields = getAllFields(sourceClass);
		List<Field> destinationFields = getAllFields(destinationClass);
		for (Field f : souceFields) {

			if (!Modifier.isPublic(f.getModifiers())) {
				f.setAccessible(true);
			}

			var sourceInstance = f.get(source);

			if (Objects.isNull(sourceInstance))
				continue;

			Optional<Field> optionalDestinationField = destinationFields.stream()
					.filter(f2 -> f2.getName().equals(f.getName())).findFirst();

			if (optionalDestinationField.isEmpty())
				continue;

			Field destinationField = optionalDestinationField.get();

			if (!Modifier.isPublic(destinationField.getModifiers())) {
				destinationField.setAccessible(true);
			}

			if (InstanceUtil.isPrimitive(f.getType()))
				destinationField.set(destination, f.get(source));
			else if (Iterable.class.isAssignableFrom(sourceInstance.getClass())
					|| sourceInstance.getClass().isArray()) {
				copyList(sourceInstance, destination, destinationField, instances);

			} else {
				Object fieldDestinationInstance = instances.get(sourceInstance);

				if (Objects.isNull(fieldDestinationInstance)) {

					fieldDestinationInstance = destinationField.getType().getConstructor().newInstance();
					instances.put(sourceInstance, fieldDestinationInstance);
					copyToDeep(sourceInstance, fieldDestinationInstance, instances);
				}

				destinationField.set(destination, fieldDestinationInstance);

			}

		}

	}


	public static boolean isLambda(Object obj) {
		if (obj == null) {
			return false;
		}
		Class<?> clazz = obj.getClass();
		return clazz.isSynthetic() && getNotDefaultMethod(clazz).isPresent();
	}

	public static Optional<Method> getNotDefaultMethod(Class<?> clazz) {

		return Stream.of(clazz.getDeclaredMethods()).filter((m) -> !m.isDefault()).findFirst();

	}

	public static <S, D> void copyToDeep(S source, D destination) throws InstantiationException {

		copyToDeep(source, destination, new HashMap());
	}

	public static <S, D> void copyToDeep(S source, D destination, Map<Object, Object> instances)
			throws InstantiationException {

		try {
			copyToDeep2(source, source.getClass(), destination, destination.getClass(), instances);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
			// ISSO NUNCA DEVERIA ACONTECER.
			throw new RuntimeException(e);
		}
	}

	private static <T, J extends T> void copyTo(T source, Class clazz, J destination, List<String> fieldsIgnoraveis)
			throws IllegalArgumentException, IllegalAccessException {

		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			if (fieldsIgnoraveis.contains(f.getName()))
				continue;

			if (!Modifier.isPublic(f.getModifiers())) {
				f.setAccessible(true);
			}
			f.set(destination, f.get(source));
		}
	}

	public static void addAnnotationAtClazz(Class<?> clazz, Class<? extends Annotation> annotationClazz) {

		AnnotationDescription description = AnnotationDescription.Builder.ofType(annotationClazz).build();
		new ByteBuddy().redefine(clazz).annotateType(description).make().load(clazz.getClassLoader(),
				ClassReloadingStrategy.fromInstalledAgent());
	}

	public static boolean isFunctionalInterface(Class<?> clazz) {
		if (!clazz.isInterface()) {
			return false;
		}

		// Check if the FunctionalInterface annotation is present
		if (clazz.isAnnotationPresent(FunctionalInterface.class)) {
			return true;
		}

		Method[] methods = clazz.getDeclaredMethods();
		int abstractMethodCount = 0;

		for (Method method : methods) {
			// Count only abstract methods
			if (Modifier.isAbstract(method.getModifiers()) && !method.isDefault()) {
				abstractMethodCount++;
			}
		}

		return abstractMethodCount == 1;
	}

	public static Object newInstanceFromGenericType(Class<?> clazz)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		ParameterizedType baseType = getGenericType(clazz);

		return ((Class<?>) baseType.getActualTypeArguments()[0]).getConstructor().newInstance();
	}

	private static ParameterizedType getGenericType(Class<?> classType) {
		if (classType == Object.class)
			return null;

		Type type = classType.getGenericSuperclass();

		if (type instanceof ParameterizedType)
			return (ParameterizedType) type;

		return getGenericType(classType.getSuperclass());
	}

	interface Loop<T> {
		void execute(T item, int index);
	}
}
