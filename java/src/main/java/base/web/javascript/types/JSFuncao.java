package base.web.javascript.types;

import base.web.javascript.JSInstanceTree;
import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public class JSFuncao extends JSElement {
	private String identificador;

	public JSFuncao(Execution exec, JSRetorno retorno) {
		super(JSElementType.FUNCAO, exec, retorno);
		this.identificador = exec.identificador;
	}

	public JSElement invoke(Object... args) {
		return this.exec.serranoJs.execFunc(identificador, args);
	}

	public String getIdentificador() {
		return identificador;
	}

	@Override
	protected <T> T get() {
		throw new UnsupportedOperationException("não implementado.");
	}

	@Override
	protected void finalize() throws Throwable {
		if (!this.ignoreFinalize)
			JSInstanceTree.delete(identificador);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "FUNCAO_" + this.identificador;
	}
}
