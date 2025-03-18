package base.web.javascript.types;

import java.math.BigDecimal;

import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public class JSNumber extends JSPrimitive {

	public JSNumber(Execution e, JSRetorno jsRetorno) {
		super(JSElementType.NUMBER, e, jsRetorno);

	}

	public BigDecimal get() {
		return this.retorno.getValue().getAsBigDecimal();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.get().toString();
	}
}
