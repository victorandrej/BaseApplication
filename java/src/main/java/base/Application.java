package base;

import java.util.*;

import javax.swing.UIManager;
import io.github.victorandrej.tinyioc.IOCBuilder;
import org.apache.commons.configuration2.Configuration;

import base.thread.LazyRunner;

import net.bytebuddy.agent.ByteBuddyAgent;
import io.github.victorandrej.tinyioc.IOC;



public class Application {

  private static final LazyRunner lazyRunner = new LazyRunner();
  public static final boolean IS_DEV_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean()
    .getInputArguments().stream().anyMatch(s -> s.contains("jdwp"));

  private static final String VERSAO_PROPRIEDADE = "serrano.version";
  public static final Long session =  new Random().nextLong();

  private static IOC ioc;
  private static String[] args;
  private static Boolean isStarted = false;
  private static long thisPID = ProcessHandle.current().pid();
   static Window window;
  private static String versao;

  public static void main(String[] args) throws Exception {

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    ByteBuddyAgent.install();
    TimeZone saoPauloTimeZone = TimeZone.getTimeZone("America/Sao_Paulo");
    TimeZone.setDefault(saoPauloTimeZone);
    try {
      long inicio = System.currentTimeMillis();
      initialize(args);
      long fim = System.currentTimeMillis();
      long tempoDecorrido = fim - inicio;

      System.out.println("Tempo de inicializacao: " + tempoDecorrido + " ms");
    } catch (Exception e) {
      e.printStackTrace();
      Runtime.getRuntime().exit(0);
    }

  }

  private static void initialize(String[] args) throws Exception {

    System.setProperty("java.awt.headless", "false");

    Application.args = new String[args.length];

    System.arraycopy(args, 0, Application.args, 0, args.length);

    ioc = IOCBuilder.configure().useScan().build();
    versao =   ioc.getInstance(Configuration.class).getString(VERSAO_PROPRIEDADE);
    if (!isStarted) {
      lazyRunner.start();
      Runtime.getRuntime().addShutdownHook(new Thread(lazyRunner::exit));
    }

    isStarted = true;

  }


  public static String versao() {
    if (!isStarted)
      throw new RuntimeException("Versao apenas desponivel apos inicializacao");
    return versao;
  }

  /**
   * Executa instrucoes apos o aplicativo iniciar, cajo ja esteja iniciado as
   * instrucoes sao chamadas imediatamente.
   *
   * @param r
   */
  public static void onStart(Runnable r) {
    lazyRunner.push(r);
  }

  public static IOC getIOC() {
    return ioc;
  }

  public static Boolean isStarted() {
    return Application.isStarted;
  }
}
