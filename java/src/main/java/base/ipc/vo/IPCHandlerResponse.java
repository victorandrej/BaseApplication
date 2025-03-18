package base.ipc.vo;

public class IPCHandlerResponse {
	String methodName;
	String beanName;
	Object value;
	boolean hasError;

	public IPCHandlerResponse(String methodName, String beanName, Object value, Boolean hasError) {
		super();
		this.methodName = methodName;
		this.beanName = beanName;
		this.value = value;
		this.hasError = hasError;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

}
