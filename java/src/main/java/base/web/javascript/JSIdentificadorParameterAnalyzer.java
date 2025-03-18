package base.web.javascript;

import base.web.javascript.JavaScript.Execution;

public class JSIdentificadorParameterAnalyzer extends ParameterAnalyzer<JSIdentificadorParameter> {

	@Override
	public Class<JSIdentificadorParameter> type() {
		return JSIdentificadorParameter.class;
	}

	@Override
	public void resolve(Execution execution, JSIdentificadorParameter param) {

		execution.identificador = param.getIdentificador();

		this.reject();

	}

}
