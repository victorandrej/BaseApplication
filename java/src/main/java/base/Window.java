package base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import base.logs.LogService;
import base.thread.LazyRunner;
import io.github.victorandrej.tinyioc.IOC;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefMessageRouter.CefMessageRouterConfig;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.*;

import base.ipc.IPCMessageHandler;
import base.jcef.CefAppInstance;
import base.util.FileUtils;

public class Window extends CefMessageRouterHandlerAdapter {
  private static final long serialVersionUID = -5570653778104813836L;
  private static final String DEV_MODE_URL = "localhost:5173";;

  String viewFolder;
  private CefApp cefApp_;
  private CefClient client_;
  private CefBrowser browser_;
  private Component browerUI_;

  private Boolean isFullScreen = false;
  private WindowFrame currFrame;
  private JPanel buttonPanel;

  private LazyRunner lazyRunner = new LazyRunner();
  private LogService logService;
  private IOC ioc;

  public Window(String viewFolder, IOC ioc) {
    this.ioc = ioc;

    Application.onStart(()->{
      this.logService = ioc.getInstance(LogService.class);
    });

    this.viewFolder = viewFolder;

    createWindow(Application.IS_DEV_MODE ? DEV_MODE_URL
      : FileUtils.getLocation().resolve(viewFolder).resolve("index.html").toString());
    this.createFrame();
    this.currFrame.configure(isFullScreen);
  }

  public CefBrowser getBrowser() {
    return this.browser_;
  }

  public void open() {

    this.currFrame.setVisible(true);

  }

  public CefClient getClient() {
    return this.client_;
  }

  public void onLoadEnd(Runnable r) {
    this.lazyRunner.push(r);
  }

  private void createWindow(String url) {

    cefApp_ = CefAppInstance.getApp();

    client_ = cefApp_.createClient();

    CefDisplayHandler consoleHandler = new CefDisplayHandlerAdapter() {

      @Override
      public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level,
                                      String message, String source, int line) {
        if(Objects.nonNull(logService))
        try {
          switch (level) {
            case LOGSEVERITY_INFO:
              logService.send(message, LogService.LogSeverity.INFO);
              break;
            case LOGSEVERITY_WARNING:
              logService.send(message, LogService.LogSeverity.WARNING);
              break;
            case LOGSEVERITY_VERBOSE:
            case LOGSEVERITY_DEFAULT:
            case LOGSEVERITY_DISABLE:
              logService.send(message, LogService.LogSeverity.DEFAUT);
              break;
            default:
              logService.send(message, LogService.LogSeverity.ERROR);
          }
          ;

        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        return false;
      }

    };

    client_.addDisplayHandler(consoleHandler);

    CefMessageRouter msgRouter = CefMessageRouter.create();
    client_.addMessageRouter(msgRouter);

    client_.addContextMenuHandler(new CefContextMenuHandler() {

      @Override
      public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
        // TODO Auto-generated method stub

      }

      @Override
      public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params,
                                          int commandId, int eventFlags) {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params,
                                      CefMenuModel model) {
        // TODO Auto-generated method stub
        model.clear();
      }
    });

    browser_ = client_.createBrowser(url, false, false);
    browerUI_ = browser_.getUIComponent();

    IPCMessageHandler.inject(browser_, client_);
    createHandler();


    if (Application.IS_DEV_MODE) {
      JButton devToolsButton = new JButton("Open DevTools");

      devToolsButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          openDebug();
        }
      });

      buttonPanel = new JPanel();
      buttonPanel.add(devToolsButton);

    }

  }

  @Override
  public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
    if (LazyRunner.ThreadState.NEW.equals(lazyRunner.getState()))
      lazyRunner.start();
    return true;
  }

  private void createHandler() {
    CefMessageRouterConfig config = new CefMessageRouterConfig("angularStarted", "angularStartedCancel");

    CefMessageRouter messageRouter = CefMessageRouter.create(config);

    messageRouter.addHandler(this, false);
    client_.addMessageRouter(messageRouter);
  }

  private void createFrame() {
    this.currFrame = new WindowFrame(buttonPanel, browerUI_);
  }

  private void createDevToolsWindow() {
    CefBrowser devToolsBrowser = browser_.getDevTools();
    Component devToolsUI = devToolsBrowser.getUIComponent();

    // Create a new JFrame to hold the DevTools UI component
    JFrame devToolsFrame = new JFrame("DevTools");
    devToolsFrame.getContentPane().add(devToolsUI, BorderLayout.CENTER);
    devToolsFrame.setSize(800, 600);
    devToolsFrame.setVisible(true);
  }

  public void openDebug() {
    this.createDevToolsWindow();
  }

  public JFrame getCurrInstance() {
    return this.currFrame;
  }

  public void toggleFullScreen() {

    var frame = currFrame;
    frame.setVisible(false);
    frame.getContentPane().remove(browerUI_);

    this.createFrame();
    isFullScreen = !isFullScreen;
    this.currFrame.configure(isFullScreen);

    this.open();
  }

  private static class WindowFrame extends JFrame {

    public WindowFrame(Component debugComponent, Component browser) {
      getContentPane().add(browser, BorderLayout.CENTER);
      if (Objects.nonNull(debugComponent))
        getContentPane().add(debugComponent, BorderLayout.SOUTH);
      addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          CefApp.getInstance().dispose();
          dispose();
        }
      });
    }

    public void configure(Boolean fullScreen) {

      if (fullScreen) {
        setUndecorated(true);
        pack();
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        device.setFullScreenWindow(this);

      } else {
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
      }

    }

  }


}
