package base.web.javascript;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import base.web.javascript.types.JSElement;

public final class JSTypeResolver {
	private JSTypeResolver() {
	}

	public static Class<?> getType(Type type) {
		if (type instanceof Class)
			return (Class<?>) type;

		if (type instanceof ParameterizedType parameterizedType) {
			Class<?> clazz = (Class<?>) parameterizedType.getRawType();
			if (Collection.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz))
				return getCollectionType(parameterizedType);
			return (Class<?>) parameterizedType.getRawType();
		}
		return null;
	}

	private static Class<?> getCollectionType(ParameterizedType type) {
		Class<?> clazz = (Class<?>) type.getRawType();
		if (List.class.equals(clazz) || Collection.class.equals(clazz)) {
			Object arg = type.getActualTypeArguments()[0];
			if (arg instanceof TypeVariable t) {
				return JSElement.class;
			} else if (arg instanceof Class c) {
				return c.equals(Object.class) ? JSElement.class : c;
			}
		}

		else if (Collection.class.isAssignableFrom(clazz)) {
			Type[] interfaceTypes = clazz.getGenericInterfaces();
			for (var t : interfaceTypes) {
				var tType = t;
				if (tType instanceof ParameterizedType) {
					ParameterizedType parametizedtType = (ParameterizedType) tType;
					if (Collection.class.isAssignableFrom((Class<?>) parametizedtType.getRawType())) {
						return getCollectionType(parametizedtType);
					}
				}

			}
			if (Objects.nonNull(clazz.getSuperclass()) && Collection.class.isAssignableFrom(clazz.getSuperclass()))
				return getCollectionType((ParameterizedType) clazz.getGenericSuperclass());

		}

		return null;
	}

}
