package base.ipc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import io.github.victorandrej.tinyioc.IOC;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import org.apache.commons.configuration2.Configuration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import base.ipc.exception.AccessDeniedException;
import base.ipc.exception.BeanNotAllowedException;
import base.ipc.model.MethodInfo;

import io.jsonwebtoken.lang.Arrays;

@Bean
public class IPCService {


  private IOC ioc;


  private PermissaoService permissaoService;


  Gson gson;

  String datePattern;

  public IPCService(IOC ioc,  PermissaoService permissaoService, Gson gson, Configuration configuration) {
    this.ioc = ioc;
    this.permissaoService = permissaoService;
    this.gson = gson;
    this.datePattern = configuration.getString("serrano.date.pattern");

  }

  public List<MethodInfo> getBeanInfo(String beanName,String clazzName) throws BeanNotAllowedException, ClassNotFoundException {

    List<MethodInfo> methodsInfos = new ArrayList<>();

    Object bean = ioc.getInstance(Class.forName(clazzName),beanName);

    Class<?> beanClass = bean.getClass();

    if (!beanClass.isAnnotationPresent(Allowed.class))
      throw new BeanNotAllowedException("Bean nao disponivel");

    Method[] beanMethods = beanClass.getMethods();

    for (Method method : beanMethods) {
      Permissao permissao = method.getDeclaredAnnotation(Permissao.class);

      if (!Modifier.isPublic(method.getModifiers()) || Objects.isNull(permissao))
        continue;

      MethodInfo methodInfo = new MethodInfo(method.getName(), beanName, clazzName, permissao.isPromise());

      for (Parameter parameter : method.getParameters()) {
        methodInfo.getParamsClass().add(parameter.getType().getName());
      }
      methodsInfos.add(methodInfo);
    }

    return methodsInfos;

  }

  public Object callMethod(IPCCallRequest iPCCall)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
    SecurityException, ClassNotFoundException, AccessDeniedException {
    Object bean = ioc.getInstance(Class.forName(iPCCall.getBeanClassName()),iPCCall.getBeanName()) ;

    Class<?> beanClass = bean.getClass();

    Method method = beanClass.getMethod(iPCCall.getMethodName(), createParametersType(iPCCall.getParametersType()));

    Permissao permission = method.getDeclaredAnnotation(Permissao.class);

    if (Objects.isNull(permission))
      throw new AccessDeniedException("Acesso não autorizado");

    List<String> values = Arrays.asList(permission.value());

    permissaoService.validarPermissoes(values);

    Object[] parsedParameters = parseParameters(method, iPCCall.getParameters());
    return method.invoke(bean, parsedParameters);

  }

  private Class<?>[] createParametersType(List<String> parametersType) throws ClassNotFoundException {
    List<Class<?>> parametersClassType = new ArrayList();

    for (String parameter : parametersType) {
      parametersClassType.add(Class.forName(parameter));

    }

    return parametersClassType.toArray(new Class<?>[0]);

  }

  private Object[] parseParameters(Method method, List<Object> parameters) {

    List<Object> parsedParameters = new ArrayList<>();
    Parameter[] methodParameters = method.getParameters();
    for (int i = 0; i < parameters.size(); i++) {
      Object parameterValue = parameters.get(i);
      var parameter = methodParameters[i];
      Type parameterClassType = parameter.getParameterizedType() instanceof ParameterizedType ? getType(methodParameters[i]) : parameter.getType();
      JsonElement elementParameter = gson.toJsonTree(parameterValue);
      Object parsedParameter = Objects.isNull(elementParameter) || (elementParameter instanceof JsonNull) ? null
        : gson.fromJson(elementParameter, parameterClassType);

      parsedParameters.add(parsedParameter);
    }

    return parsedParameters.toArray();
  }

  private static Type getType(Parameter parameter) {
    return (Type) new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
        return ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments();
      }

      @Override
      public Type getRawType() {
        return parameter.getType();
      }

      @Override
      public Type getOwnerType() {
        return null;
      }
    };
  }
}
