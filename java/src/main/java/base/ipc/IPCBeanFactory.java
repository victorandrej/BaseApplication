package base.ipc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;


import base.WindowFactory;
import io.github.victorandrej.tinyioc.config.*;
import io.github.victorandrej.tinyioc.order.BeanOrder;
import io.github.victorandrej.tinyioc.order.Ring0;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import io.github.victorandrej.tinyioc.steriotypes.BeanFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

@Bean(priority = Ring0.class, order = BeanOrder.BEFORE, classOrder = WindowFactory.class)
public class IPCBeanFactory implements BeanFactory {

  PromiseService promiseService;


  public IPCBeanFactory(PromiseService promiseService, ConfigurationImpl configuration) {
    this.promiseService = promiseService;
    init(configuration);
  }


  public void init(ConfigurationImpl configuration) {

    var beans = configuration.getBeans();
    for (var bean : beans)
      if (bean.getBeanClass().isAnnotationPresent(Allowed.class)) {
        bean.setBeanClass(proxyClass(bean.getBeanClass()));
      }


  }


  @Override
  public void create(Configuration configuration) throws Exception {

  }

  public Class<?> proxyClass(Class<?> clazz) {
    Constructor c = clazz.getConstructors()[0];
    var interceptor = new IPCInterceptor(promiseService);
    var builder = new ByteBuddy().subclass(clazz).annotateType(clazz.getAnnotations())
      .constructor(ElementMatchers.any()).intercept(MethodCall.invoke(c).withAllArguments());

    MethodDefinition<?> methodDefinition = null;
    Boolean isFirst = true;
    Boolean changed = false;
    for (Method method : clazz.getDeclaredMethods()) {

      if (method.isAnnotationPresent(Export.class)) {
        changed = true;
        methodDefinition = (isFirst ? builder : methodDefinition)
          .method(ElementMatchers.named(method.getName())
            .and(ElementMatchers.takesArguments(method.getParameterTypes())))
          .intercept(MethodDelegation.to(interceptor)).annotateMethod(method.getDeclaredAnnotations());

        if (isFirst) {
          isFirst = false;
        }

      }

    }

    return (changed ? methodDefinition : builder).make().load(clazz.getClassLoader()).getLoaded();
  }


  public class IPCInterceptor {

    PromiseService promiseService;

    public IPCInterceptor(PromiseService promiseService) {
      this.promiseService = promiseService;
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> superMethod, @Origin Method method) throws Throwable {

      Export ex = method.getAnnotation(Export.class);
      Boolean hasError = false;
      Object value = null;

      try {
        return value = superMethod.call();
      } catch (Exception e) {
        value = e;
        hasError = true;
        throw (Throwable) value;
      } finally {
        if (Objects.nonNull(ex) && ex.isPromise())
          if (hasError)
            promiseService.reject(value, method);
          else
            promiseService.resolve(value, method);
      }
    }
  }

}
