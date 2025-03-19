package base.ipc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface IpcChain {

 default void doChain()throws  Exception{}

  public void doChain(IpcChain chain, Method method) throws  Exception;

}
