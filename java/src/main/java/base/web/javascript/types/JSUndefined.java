package base.web.javascript.types;

import base.types.Null;
import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public class JSUndefined extends JSPrimitive {

	public JSUndefined(Execution exec, JSRetorno jsRetorno ) {
		super(JSElementType.UNDEFINED, exec, jsRetorno);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sempre retorna nulo
	 */
	@Override
	public Null get() {

		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "NULL";
	}

}
