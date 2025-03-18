package base.ipc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


import io.github.victorandrej.tinyioc.exception.NoSuchBeanException;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import base.Application;
import base.Window;

import base.exception.SerranoException;
import base.ipc.exception.AccessDeniedException;
import base.ipc.exception.BeanNotAllowedException;
import base.ipc.model.MethodInfo;
import base.ipc.vo.IPCHandlerResponse;
import base.ipc.vo.IPCResponse;
import base.util.ResourceUtils;
import base.web.javascript.JavaScript;



@Bean()
public class IPC {
  private static final String SERRANO_JS_DIR = "javaScript/serrano.js";



  IPCService ipcService;

  Gson gson;

  Window window;

  JavaScript js;

  Boolean logarExcecao;
  private volatile boolean injected = false;
  private List<Runnable> injections = new ArrayList<>();

  public IPC(
    IPCService ipcService,
    Gson gson,
    Window window,
    JavaScript js,
    Configuration configuration) throws Exception {

    this.ipcService = ipcService;
    this.gson = gson;
    this.window = window;
    this.js = js;
    this.logarExcecao = configuration.getBoolean("   serrano.log.exception", false);
  }


  public void startSerranoScript() throws IOException, URISyntaxException, Exception {
    ResourceUtils.getResourceFiles(SERRANO_JS_DIR).execute((r) -> {
      js.injectScript(IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8.name()),
        window.getBrowser());
      injected = true;
      injections.forEach(ru -> ru.run());
    });

  }

  public Boolean isAlive() {
    return Application.isStarted();
  }


  public List<MethodInfo> getBeanInfo(String beanName, String className) throws SerranoException {

    try {
      return ipcService.getBeanInfo(beanName, className);
    } catch (NoSuchBeanException e) {
      throw new SerranoException("", "Bean nao existe Bean Class: " + className + " Bean Name: " + beanName);
    } catch (BeanNotAllowedException e) {
      throw new SerranoException("", "Bean nao disponivel");
    } catch (ClassNotFoundException e) {
      throw new SerranoException("", "Classe :" + className + " nao existe");
    }

  }

  @SuppressWarnings("unchecked")
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

  public Object callMethod(IPCCallRequest iPCCall)
    throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
    SecurityException, ClassNotFoundException, AccessDeniedException {

    return new IPCResponse(ipcService.callMethod(iPCCall), iPCCall.getUuid(), false);

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
