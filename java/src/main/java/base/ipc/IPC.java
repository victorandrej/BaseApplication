package base.ipc;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.github.victorandrej.tinyioc.IOC;
import io.github.victorandrej.tinyioc.annotation.Inject;
import io.github.victorandrej.tinyioc.exception.NoSuchBeanException;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import org.apache.commons.configuration2.Configuration;

import com.google.gson.Gson;
import base.Application;
import base.Window;

import base.exception.SerranoException;
import base.ipc.exception.BeanNotAllowedException;
import base.ipc.model.MethodInfo;
import base.ipc.vo.IPCHandlerResponse;
import base.ipc.vo.IPCResponse;
import base.web.javascript.JavaScript;


@Bean()
@Service
public class IPC {
  private static final String SERRANO_JS_DIR = "javaScript/serrano.js";


  IOC ioc;

  Gson gson;

  Window window;

  JavaScript js;

  private Collection<IpcChain> ipcChain;

  Boolean logarExcecao;
  private volatile boolean injected = false;
  private List<Runnable> injections = new ArrayList<>();

  public IPC(
    IOC ioc,
    Gson gson,
    Window window,
    JavaScript js,
    Configuration configuration,
    @Inject(optional = true) Collection<IpcChain> ipcChain
  ) throws Exception {
    this.ipcChain = Objects.isNull(ipcChain) ? new ArrayList<>() : ipcChain;
    this.ioc = ioc;
    this.gson = gson;
    this.window = window;
    this.js = js;
    this.logarExcecao = configuration.getBoolean("   serrano.log.exception", false);
  }

  public Boolean isAlive() {
    return Application.isStarted();
  }

  @Export
  public List<MethodInfo> getBeanInfo(String beanName, String className) throws SerranoException {

    try {

      List<MethodInfo> methodsInfos = new ArrayList<>();
      Object bean = ioc.getInstance(Class.forName(className), beanName);
      Class<?> beanClass = bean.getClass();

      if (!beanClass.isAnnotationPresent(Service.class))
        throw new BeanNotAllowedException("Bean nao disponivel");

      Method[] beanMethods = beanClass.getMethods();

      for (Method method : beanMethods) {
        Export export = method.getDeclaredAnnotation(Export.class);

        if (!Modifier.isPublic(method.getModifiers()) || Objects.isNull(export))
          continue;

        MethodInfo methodInfo = new MethodInfo(method.getName(), beanName, className, export.isPromise());

        for (Parameter parameter : method.getParameters()) {
          methodInfo.getParamsClass().add(parameter.getType().getName());
        }
        methodsInfos.add(methodInfo);
      }

      return methodsInfos;
    } catch (NoSuchBeanException e) {
      throw new SerranoException("", "Bean nao existe Bean Class: " + className + " Bean Name: " + beanName);
    } catch (BeanNotAllowedException e) {
      throw new SerranoException("", "Bean nao disponivel");
    } catch (ClassNotFoundException e) {
      throw new SerranoException("", "Classe :" + className + " nao existe");
    }

  }


  public Object call(IPCCallRequest request) throws Exception {

    try {

      return callMethod(request);
    } catch (Exception e) {

      if (logarExcecao) {
        System.err.println(new Gson().toJson(request));
        e.printStackTrace();
      }
      throw e;
    }

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

  public Object callMethod(IPCCallRequest iPCCall)
    throws Exception {

    Object bean = ioc.getInstance(Class.forName(iPCCall.getBeanClassName()), iPCCall.getBeanName());
    Class<?> beanClass = bean.getClass();
    Method method = beanClass.getMethod(iPCCall.getMethodName(), createParametersType(iPCCall.getParametersType()));

    Object[] parsedParameters = parseParameters(method, iPCCall.getParameters());

    AtomicReference<Object> value = new AtomicReference<>();


    IpcChain defaultChain = (c, m,p) -> {
      value.set(m.invoke(bean, parsedParameters));
    };

    doChain(method, defaultChain,parsedParameters);

    return new IPCResponse(value.get(), iPCCall.getUuid(), false);

  }


  private IpcChain createChain(Method method, Iterator<IpcChain> chains,Object[]parameters) {
    return new IpcChain() {
      @Override
      public void doChain(IpcChain chain, Method method,Object[] parameters) throws Exception {}

      @Override
      public void doChain() throws Exception {
        if (chains.hasNext())
          chains.next().doChain(createChain(method, chains,parameters), method,parameters);
      }
    };
  }

  private void doChain(Method method, IpcChain defaultChain, Object[] parameters) throws Exception {
    var chains = new ArrayList<IpcChain>();
    chains.addAll(this.ipcChain);
    chains.add(defaultChain);
    createChain(method, chains.iterator(),parameters).doChain();
  }

  public void send(String methodName, String beanName, Object valor, Boolean hasError, IPCMessageHandler handler) {
    handler.send(new IPCHandlerResponse(methodName, beanName, valor, hasError));
  }

  public boolean isInjected() {
    return this.injected;
  }

  public void onInject(Runnable r) {
    this.injections.add(r);
  }
}
