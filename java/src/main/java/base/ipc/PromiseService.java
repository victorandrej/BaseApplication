package base.ipc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import base.Application;
import io.github.victorandrej.tinyioc.IOC;
import io.github.victorandrej.tinyioc.config.BeanMetadado;

import io.github.victorandrej.tinyioc.order.BeanOrder;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import org.cef.browser.CefBrowser;

@Bean(beanName = "ipcPromise", classOrder = IPCBeanFactory.class, order = BeanOrder.BEFORE)
public class PromiseService {


  IPC ipc;
  IOC ioc;

  public PromiseService(IPC ipc, IOC ioc) {
    this.ipc = ipc;
    this.ioc = ioc;
  }


  public <T> void resolve(T valor, Method method) {
    IPCMessageHandler.getHandlers().entrySet().forEach(e -> {

      try {

        send(valor, method, Boolean.FALSE, null, e.getValue());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
  }

  public <T> void reject(T valor, Method method) {
    IPCMessageHandler.getHandlers().entrySet().forEach(e -> {

      try {

        send(valor, method, Boolean.TRUE, null, e.getValue());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
  }

  public <T> void resolve(T valor, Method method, CefBrowser browser) {

    this.send(valor, method, Boolean.FALSE, browser, null);
  }

  public <T> void reject(T valor, Method method, CefBrowser browser) {

    this.send(valor, method, Boolean.TRUE, browser, null);
  }

  private <T> void send(T valor, Method method, Boolean hasError, CefBrowser browser, IPCMessageHandler handler) {

    Runnable r = () -> {

      Collection<BeanMetadado> beans = ioc.getInstancesCollectionMetadado(method.getDeclaringClass());

      if (Objects.nonNull(beans)) {
        for (var bean : beans)
          ipc.send(method.getName(), bean.getName(), valor, hasError,
            Objects.nonNull(handler) ? handler : IPCMessageHandler.getHandler(browser));
      }
    };

    if (ipc.isInjected())
      Application.onStart(r);
    else
      ipc.onInject(r);

  }
}
