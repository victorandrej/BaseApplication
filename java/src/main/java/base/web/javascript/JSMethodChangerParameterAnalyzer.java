package base.web.javascript;

import io.github.victorandrej.tinyioc.steriotypes.Bean;


import base.web.javascript.JavaScript.Execution;

@Bean
public class JSMethodChangerParameterAnalyzer extends ParameterAnalyzer<JSMethodReturnTypeParameter> {

	@Override
	public Class<JSMethodReturnTypeParameter> type() {
		return JSMethodReturnTypeParameter.class;
	}

	@Override
	public void resolve(Execution execution, JSMethodReturnTypeParameter param) {
		execution.returnType = param.getType();
		reject();
	}

}
