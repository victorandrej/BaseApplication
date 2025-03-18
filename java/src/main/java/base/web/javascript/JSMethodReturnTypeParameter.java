package base.web.javascript;

import java.lang.reflect.Type;

public class JSMethodReturnTypeParameter {
	private Type type;

	public JSMethodReturnTypeParameter(Type type) {
		this.type = type;
	}

	public Type getType() {
		return this.type;
	}
}
