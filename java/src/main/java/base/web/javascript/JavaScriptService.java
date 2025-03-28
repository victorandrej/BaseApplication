package base.web.javascript;

import java.util.List;

import base.ipc.Export;
import base.ipc.Service;

import base.web.javascript.JavaScript.CallBack;
import io.github.victorandrej.tinyioc.steriotypes.Bean;

@Service
@Bean
public class JavaScriptService {


	JavaScript js;
  public  JavaScriptService(JavaScript js){
    this.js = js;
  }


  @Export
	public void resolve(JSRetorno jsRetorno,Boolean hasError)   {
		js.resolve(jsRetorno,hasError);
	}


  @Export
	public CallBack resolveCallBack(String id,List<JSRetorno> parameters) throws Exception   {
		return js.resolveCallBack(id, parameters);
	}

}
