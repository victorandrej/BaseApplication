package base.web.javascript.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public class JSDate extends JSPrimitive {

	public JSDate(Execution e, JSRetorno jsRetorno) {
		super(JSElementType.DATE, e, jsRetorno);
	}

	public Date get() {
		try {
			return new SimpleDateFormat().parse(this.retorno.getValue().getAsString());
		} catch (ParseException e) {
			throw new RuntimeException(e);

		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.retorno.getValue().getAsString();
	}
}
