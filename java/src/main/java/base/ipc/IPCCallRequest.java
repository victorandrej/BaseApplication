package base.ipc;

import java.util.ArrayList;
import java.util.List;

public class IPCCallRequest {
	private String beanName;
	private String methodName;
	private List<String> parametersType = new ArrayList<>();
	private List<Object> parameters;
	private String uuid;
  private String beanClassName;

	public String getBeanName() {
		return beanName;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<String> getParametersType() {
		return parametersType;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public String getUuid() {
		return uuid;
	}


  public String getBeanClassName() {
    return  this.beanClassName;
  }
}
