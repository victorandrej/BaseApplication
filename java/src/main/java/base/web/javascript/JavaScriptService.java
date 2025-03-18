package base.web.javascript;

import java.util.List;

import base.ipc.Allowed;
import base.ipc.Permissao;
import base.web.javascript.JavaScript.CallBack;
import io.github.victorandrej.tinyioc.steriotypes.Bean;

@Allowed
@Bean
public class JavaScriptService {


	JavaScript js;
  public  JavaScriptService(JavaScript js){
    this.js = js;
  }

	@Permissao(value = Permissao.TODOS)
	public void resolve(JSRetorno jsRetorno,Boolean hasError)   {
		js.resolve(jsRetorno,hasError);
	}


	@Permissao(value = Permissao.TODOS)
	public CallBack resolveCallBack(String id,List<JSRetorno> parameters) throws Exception   {
		return js.resolveCallBack(id, parameters);
	}

}
