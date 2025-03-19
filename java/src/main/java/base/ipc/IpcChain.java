package base.ipc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * um filtro de execucao do IPC, esse filtro e chamado antes da execucao do servico
 */
public interface IpcChain {

 default void doChain()throws  Exception{}

  public void doChain(IpcChain chain, Method method,Object[] parameters) throws  Exception;

}
