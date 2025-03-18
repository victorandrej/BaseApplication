package base.web.javascript;

import base.web.javascript.JavaScript.Execution;

public abstract class ParameterAnalyzer<T> {
	private boolean isRejected = false;

	public abstract Class<T> type();

	protected void reject() {
		this.isRejected = true;
	}

	public abstract void resolve(Execution execution, T param);

	/**
	 * apos chamada, valor e modificado para false
	 *
	 * @return
	 */
	public boolean isRejected() {
		try {
			return this.isRejected;
		} finally {
			this.isRejected = false;
		}
	}

}
