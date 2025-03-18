package base.util;

import base.Application;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;


public final class ClassUtil {
    private ClassUtil() {
    }

    /**
     * retorna todas as classes contida no pacote
     *
     * @param rootClassPackage classe do pacote que deve ser escaneado
     * @return
     */
    public static Set<Class> findAllClasses(Class<?> rootClassPackage) {
        InputStream stream = rootClassPackage.getClassLoader().getSystemClassLoader()
                .getResourceAsStream(rootClassPackage.getPackageName().replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, rootClassPackage.getPackageName()))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        className = className.substring(0, className.lastIndexOf('.'));
        String fullClassName = packageName.isEmpty() ? className : (packageName + "."
                + className);
        try {
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }


    public static <T extends Throwable> void sneakyThrow(Runnable r, java.lang.Runnable... runnables) throws T {
      Boolean hasError = false;
       try {
            r.run();
        } catch (Exception e) {
            if(Application.IS_DEV_MODE)
                e.printStackTrace();
            throw (T) e;
        } finally {
           if(!hasError)
               return;

            for (var finaly : runnables)
                finaly.run();
        }

    }

    public static <T extends Throwable> void sneakyThrow(Throwable exception) throws T {
        throw (T) exception;
    }


    public interface Runnable {
        public void run() throws Exception;
    }

}
