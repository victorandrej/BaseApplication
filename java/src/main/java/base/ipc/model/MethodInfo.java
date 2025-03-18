package base.ipc.model;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
	private String method;
	private String beanName;
	private Boolean isPromise;
  private String beanClassName;
	private List<String> paramsClass;

	public MethodInfo(String method, String beanName,String beanClassName,Boolean isPromise) {
		this.method = method;
		this.beanName = beanName;
		this.isPromise = isPromise;
    this.beanClassName = beanClassName;
		this.paramsClass = new ArrayList<>();
	}

	public String getMethod() {
		return method;
	}

	public String getBeanName() {
		return beanName;
	}

	public List<String> getParamsClass() {
		return paramsClass;
	}

	public Boolean isPromise() {
		return this.isPromise;
	}


}
