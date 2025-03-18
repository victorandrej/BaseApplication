package base.web.javascript.types;

import org.cef.browser.CefBrowser;

import base.web.javascript.JSRetorno;
import base.web.javascript.JavaScript.Execution;

public abstract class JSElement {

	private JSElementType type;

	protected Execution exec;
	JSRetorno retorno;

	protected boolean ignoreFinalize;

	private CefBrowser contexto;

	public JSElement(JSElementType type, Execution exec, JSRetorno retorno) {
		this.type = type;
		this.exec = exec;
		this.contexto = exec.browser;
		this.retorno = retorno;
	}

	public JSElementType getType() {
		return this.type;
	}

	public Execution getExec() {
		return exec;
	}

	public JSRetorno getRetorno() {
		return retorno;
	}

	protected abstract <T> T get();

	public void ignoreFinalize(boolean b) {
		this.ignoreFinalize = b;

	}

	public CefBrowser getContexto() {
		return contexto;
	}

	@Override
	public abstract String toString();

}
