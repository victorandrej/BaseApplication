package base.web.javascript.types;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Argument {
	Parameter param;
	Method method;
	Object value;

	public Argument(Parameter param, Method method, Object value) {
		super();
		this.param = param;
		this.method = method;
		this.value = value;
	}

	public Parameter getParam() {
		return param;
	}

	public Method getMethod() {
		return method;
	}

	public Object getValue() {
		return value;
	}

}
