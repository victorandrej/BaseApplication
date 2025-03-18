package base.web.javascript.types;

import java.lang.reflect.Type;

import base.web.javascript.JSInstanceTree;
import base.web.javascript.JSMethodReturnTypeParameter;
import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;


public class JSObject extends JSElement {
	private String identificador;

	public JSObject(Execution exec, JSRetorno retorno) {
		super(JSElementType.OBJECT, exec, retorno);
		this.identificador = exec.identificador;
		JSInstanceTree.put(null, identificador, this.exec.serranoJs);
	}

	public <T> T invoke(String prop, Type returnType, Object... args) {
		Object o = exec.serranoJs.exec(identificador, prop, args, new JSMethodReturnTypeParameter(returnType));
		if (JSFuncao.class.isAssignableFrom(o.getClass())) {
			JSFuncao func = (JSFuncao) o;
			JSInstanceTree.put(identificador, func.getIdentificador(), exec.serranoJs);
		}

		return (T) o;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!this.ignoreFinalize) {

			JSInstanceTree.delete(identificador);
		}
	}

	@Override
	protected <T> T get() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIdentificador() {
		return identificador;
	}

	public <T> T to(Class<T> type) {
		try {
			return (T) this.exec.js.resolveInstance(this, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.exec.serranoJs.objectToString(identificador);
	}

}
