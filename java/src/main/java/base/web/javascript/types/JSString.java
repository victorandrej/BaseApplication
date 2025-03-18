package base.web.javascript.types;

import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public class JSString extends JSPrimitive {

	public JSString(Execution e, JSRetorno jsRetorno) {
		super(JSElementType.STRING, e, jsRetorno);
	}

	public String get() {
		return this.retorno.getValue().getAsString();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.get();
	}

}
