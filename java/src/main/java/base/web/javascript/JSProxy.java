package base.web.javascript;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import base.web.javascript.types.JSFuncao;
import base.web.javascript.types.JSObject;

public class JSProxy implements InvocationHandler {
	JavaScript js;

	public JSProxy(JavaScript js) {
		this.js = js;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if (JSObject.class.isAssignableFrom(proxy.getClass()))
			return resolveAsObject((JSObject) proxy, method, args);
		else if (JSFuncao.class.isAssignableFrom(proxy.getClass()))
			return resolveAsFunction((JSFuncao) proxy, method, args);
		else
			throw new RuntimeException("Não implementado este tipo " + proxy.getClass());

	}

	private Object resolveAsObject(JSObject object, Method method, Object[] args) throws Exception {
		return object.invoke(method.getName(), method.getGenericReturnType(), args);
	}

	private Object resolveAsFunction(JSFuncao funcao, Method method, Object[] args) throws Exception {
		return js.resolveInstance(funcao.invoke(args),method.getGenericReturnType());
	}

}
