package base.ipc.vo;

public class IPCResponse {
	private Object value;
  private String uuid;
  private Boolean hasError;
	public IPCResponse(Object response,String uuid,boolean hasError) {

		this.value = response;
    this.uuid = uuid;
    this.hasError = hasError;
	}

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  public Boolean getHasError(){
    return  this.hasError;
  }

  public void setHasError(Boolean hasError) {
    this.hasError = hasError;
  }

  public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
