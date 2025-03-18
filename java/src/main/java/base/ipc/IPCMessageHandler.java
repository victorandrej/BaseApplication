package base.ipc;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import base.Application;
import base.util.ClassUtil;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefMessageRouter.CefMessageRouterConfig;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;


import com.google.gson.Gson;
import base.ipc.vo.IPCException;
import base.ipc.vo.IPCHandlerResponse;
import base.web.SerranoJS;
import base.web.javascript.JavaScript;

public class IPCMessageHandler extends CefMessageRouterHandlerAdapter {
  private static ThreadLocal<IPCMessageHandler> currMessageHandler = new ThreadLocal();
  private static ThreadLocal<CefBrowser> currBrowser = new ThreadLocal();
  private static Map<CefBrowser, IPCMessageHandler> messageHandlers = Collections.synchronizedMap(new HashMap<>());
  private Collection<IpcChain> ipcChain;

  public static IPCMessageHandler getCurrMessageHandler() {
    return currMessageHandler.get();
  }

  public static Map<CefBrowser, IPCMessageHandler> getHandlers() {

    return messageHandlers;
  }

  public static CefBrowser getCurrBrowser() {
    return currBrowser.get();
  }

  public static IPCMessageHandler getHandler(CefBrowser b) {
    return messageHandlers.get(b);
  }

  public static void inject(CefBrowser browser, CefClient client) {
    CefMessageRouterConfig config = new CefMessageRouterConfig("ipc", "ipcCancel");

    CefMessageRouter messageRouter = CefMessageRouter.create(config);
    var handler = new IPCMessageHandler(browser);
    messageHandlers.put(browser, handler);
    messageRouter.addHandler(handler, true);
    client.addMessageRouter(messageRouter);
    Application.onStart(() -> {
      var ioc = Application.getIOC();
      handler.js = ioc.getInstance(JavaScript.class);
      handler.ipc = ioc.getInstance(IPC.class);
      handler.gson = ioc.getInstance(Gson.class);
      handler.ipcChain = ioc.getInstancesCollection(IpcChain.class);
    });
  }


  private CefBrowser browser;

  SerranoJS serranoJS;


  JavaScript js;

  IPC ipc;

  Gson gson;


  private IPCMessageHandler(CefBrowser browser) {
    this.browser = browser;
  }

  int threadsCriadas = 0;

  @Override
  public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent,
                         CefQueryCallback callback) {
    if (request.equals("isAlive")) {
      callback.success(Boolean.toString(Application.isStarted()));
      return true;
    }
    if (Objects.isNull(serranoJS)) {
      Boolean[] finished = new Boolean[1];
      finished[0] = false;
      Application.onStart(() -> {
        serranoJS = js.facade(SerranoJS.class, "serrano", browser);
        finished[0] = true;
      });

      while (!finished[0]) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    }
    var ipcRequest = gson.fromJson(request, IPCCallRequest.class);

    if ((ipcRequest.getBeanName().equals("javaScriptService") && ipcRequest.getMethodName().equals("resolve"))
      || (ipcRequest.getBeanName().equals("iPC") && ipcRequest.getMethodName().equals("startSerranoScript"))

    ) {
      ClassUtil.sneakyThrow(()->gson.toJson(ipc.call(ipcRequest))); ;
      callback.success("");
      return true;
    }
    new Thread(() -> {

      call(browser, ipcRequest, null);
    }).start();

    return true;
  }

  private void call(CefBrowser browser, IPCCallRequest ipcRequest, @Nullable CefQueryCallback callback) {
    currMessageHandler.set(this);
    Boolean hasError = false;
    AtomicReference<String> json = new AtomicReference<>();
    try {

      IpcChain lastChain = (c)->{
        json.set(gson.toJson(ipc.call(ipcRequest)));
      };

      var chains = new ArrayList<IpcChain>();
      chains.addAll(this.ipcChain);
      chains.add(lastChain);
      var iter = chains.iterator();
      var i = iter.next();
      i.chain(iter.hasNext() ? iter.next() : null);
    } catch (Exception e) {
      e.printStackTrace();
      json.set( gson.toJson(new IPCException(getNonNullCause(e))));
      hasError = true;
    }
    currMessageHandler.remove();
    if (Objects.nonNull(ipcRequest.getUuid()))
      serranoJS.resolveRequests(ipcRequest.getUuid(), json.get(), hasError);

  }

  private Throwable getNonNullCause(Throwable t) {
    var newT = t;
    while ((Objects.isNull(newT.getMessage()) || newT.getMessage().isBlank()) && Objects.nonNull(newT.getCause())) {
      t = newT;
      newT = t.getCause();

    }

    return newT;
  }

  @Override
  public void onQueryCanceled(CefBrowser browser, CefFrame frame, long queryId) {

    System.out.println("Query canceled: " + queryId);
  }

  public void send(IPCHandlerResponse response) {
    String res = gson.toJson(response);

    browser.executeJavaScript("window.serrano.resolveHandlerCall(" + res + ")", browser.getURL(), 0);
  }

}
