package base.ipc;

public interface IpcChain {
  public void chain(IpcChain chain) throws Exception;
}
