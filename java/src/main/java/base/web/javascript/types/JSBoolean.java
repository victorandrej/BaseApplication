package base.web.javascript.types;

import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public class JSBoolean extends JSPrimitive {

	public JSBoolean(Execution exec, JSRetorno jsRetorno) {
		super(JSElementType.BOOLEAN, exec, jsRetorno);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean get() {

		return this.retorno.getValue().getAsBoolean();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  Boolean.toString(this.get());
	}

}
