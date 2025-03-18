package base.web.javascript;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

import io.github.victorandrej.tinyioc.steriotypes.Bean;
import org.apache.commons.io.IOUtils;
import org.cef.browser.CefBrowser;

import com.google.gson.Gson;
import base.util.InstanceUtil;
import base.util.ResourceUtils;
import base.web.SerranoJS;
import base.web.javascript.types.JSArray;
import base.web.javascript.types.JSBoolean;
import base.web.javascript.types.JSDate;
import base.web.javascript.types.JSElement;
import base.web.javascript.types.JSElementType;
import base.web.javascript.types.JSFuncao;
import base.web.javascript.types.JSNumber;
import base.web.javascript.types.JSObject;
import base.web.javascript.types.JSString;
import base.web.javascript.types.JSUndefined;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

@Bean
public class JavaScript {



	private static final String IDENTIFICADOR_TEMPLATE = "__$$IDENTIFICADOR$$__";
	private static final String FUNCTION_TEMPLATE = "__$$FUNCTION$$__";
	private static final String SCRIPT_TEMPLATE = "__$$SCRIPT$$__";
	private static final String FUNCTION_CALL_TEMPLATE_JS_DIR = "javaScript/template/functionCallTemplate.js";
	private static final String SCRIPT_INJECTION_TEMPLATE_DIR = "javaScript/template/scriptInjectionTemplate.js";
	private static final String FUNCTION_CALLBACK_TEMPLATE = "javaScript/template/functionCallBackTemplate.js";
	private String execFuncTemplate;

	private String scriptInjectionTemplate;

	private String functionCallBackTemplate;

	private Map<CefBrowser, SerranoJS> serranoJSInstances = Collections
			.synchronizedMap(new HashMap<CefBrowser, SerranoJS>());

	private Gson gson;


	private Collection<ParameterAnalyzer> parametersAnalyzer;

	private Map<String, Execution> executions = Collections.synchronizedMap(new HashMap<String, Execution>());

	private Map<String, CallBack> callBacks = Collections.synchronizedMap(new HashMap<String, CallBack>());

	public JavaScript( Gson gson,Collection<ParameterAnalyzer> parametersAnalyzer) throws IOException, URISyntaxException, Exception {
    this.gson =gson;
    this.parametersAnalyzer = parametersAnalyzer;
		ResourceUtils.getResourceFiles(FUNCTION_CALL_TEMPLATE_JS_DIR).execute((r) -> {
			this.execFuncTemplate = IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8.name());

		});
		ResourceUtils.getResourceFiles(SCRIPT_INJECTION_TEMPLATE_DIR).execute((r) -> {
			this.scriptInjectionTemplate = IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8.name());

		});

		ResourceUtils.getResourceFiles(FUNCTION_CALLBACK_TEMPLATE).execute((r) -> {
			this.functionCallBackTemplate = IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8.name());
		});
	}

	public String callback(Object o, CefBrowser browser) throws NoSuchMethodException, SecurityException {

		var exec = new Execution();
		exec.step = ExecutionStep.NEW;
		exec.js = this;
		exec.serranoJs = this.initSerranoJS(browser);
		exec.browser = browser;

		return funcCall(o, exec);
	}

	private Object createInstance(Execution exe, JSRetorno jsRetorno, Type type) throws Exception {

		JSElement element = createJSElement(exe, jsRetorno);
		return resolveInstance(element, type);
	}

	private JSElement createJSElement(Execution exe, JSRetorno jsRetorno) {
		return switch (jsRetorno.getType()) {
		case DATE -> new JSDate(exe, jsRetorno);
		case NUMBER -> new JSNumber(exe, jsRetorno);
		case OBJECT -> new JSObject(exe, jsRetorno);
		case STRING -> new JSString(exe, jsRetorno);
		case BOOLEAN -> new JSBoolean(exe, jsRetorno);
		case FUNCAO -> new JSFuncao(exe, jsRetorno);
		case UNDEFINED -> new JSUndefined(exe, jsRetorno);
		case ARRAY -> new JSArray(exe, jsRetorno);

		};
	}

	private String createJSElementParameter(JSElement o, Execution exe) {

		if (!exe.browser.equals(o.getContexto()))
			return createJSElementParameterAnotherContext(o, exe);

		if (o instanceof JSObject jsObject)
			return "serrano.objetos.get(" + jsObject.getIdentificador() + "),";
		else if (o instanceof JSFuncao jsFuncao)
			return "serrano.objetos.get(" + jsFuncao.getIdentificador() + "),";
		else if (o instanceof JSArray jsArray)
			return "serrano.objetos.get(" + jsArray.getIdentificador() + "),";
		else
			return o.toString();
	}

	private String createJSElementParameterAnotherContext(JSElement o, Execution exe) {
		return gson.toJson(o);
	}

	private String createParameters(Execution exe, Object[] args) throws Exception {

		String argsString = "";

		if (Objects.nonNull(args)) {
			for (int i = 0; i < args.length; i++) {
				Object o = args[i];
				boolean isRejected = false;
				for (var a : parametersAnalyzer) {
					if (Objects.isNull(o) || !a.type().equals(o.getClass()))
						continue;
					a.resolve(exe, o);
					isRejected = isRejected ? isRejected : a.isRejected();
				}

				if (isRejected)
					continue;

				if (Objects.isNull(o)) {
					argsString += "undefined";
				} else if (o.getClass().isArray() || Iterable.class.isAssignableFrom(o.getClass())) {
					Object[] oArg;

					if (o.getClass().isArray()) {
						int length = Array.getLength(o);
						oArg = new Object[length];
						for (int index = 0; index < length; index++) {
							oArg[index] = Array.get(o, index);
						}
					} else {
						oArg = StreamSupport.stream(((Iterable<?>) o).spliterator(), false).toArray();
					}

					argsString += "[" + createParameters(exe, oArg) + "]";
				} else if (o instanceof JSElement) {

					argsString += createJSElementParameter((JSElement) o, exe) + "";
				} else if (InstanceUtil.isLambda(o)) {

					argsString += funcCall(o, exe);
				} else {
					argsString += gson.toJson(o);
				}
				argsString += ",";
			}

			argsString =argsString.isBlank()? argsString: argsString.substring(0, argsString.length() - 1);
		}
		return argsString;

	}

	private Object createPrimitive(JSElement element) throws Exception {
		return switch (element.getType()) {
		case DATE -> ((JSDate) element).get();
		case NUMBER -> ((JSNumber) element).get();
		case OBJECT -> throw new Exception("retorno do tipo Objeto, era espedado uma interface como retorno no metodo");
		case STRING -> ((JSString) element).get();
		case BOOLEAN -> ((JSBoolean) element).get();
		case FUNCAO -> throw new Exception("retorno do tipo Funcao, espedado uma interface como retorno no metodo");
		case UNDEFINED -> ((JSUndefined) element).get();
		case ARRAY -> ((JSArray) element).get();

		};
	}

	private Class<?> createProxyClass(Class<?> returnType, JSElement element)
			throws NoSuchMethodException, SecurityException {
		Class<?> superClass = getElementClassType(element);

		return new ByteBuddy().subclass(superClass).implement(returnType)
				.intercept(MethodCall
						.invoke(superClass.getConstructor(Execution.class, JSRetorno.class)).withAllArguments())
				.method(ElementMatchers.not(ElementMatchers.isDeclaredBy(JSFuncao.class))

						.and(ElementMatchers.not(ElementMatchers.isDeclaredBy(JSObject.class)))
						.and(ElementMatchers.not(ElementMatchers.isDefaultMethod())))
				.intercept(InvocationHandlerAdapter.of(new JSProxy(this)))

				.make().load(JSProxy.class.getClassLoader()).getLoaded();
	}

	private Object createProxyInstance(Class<?> returnType, JSElement element)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Class<?> c = createProxyClass(returnType, element);
		element.ignoreFinalize(true);
		Constructor constru = c.getConstructor(Execution.class, JSRetorno.class);

		return constru.newInstance(element.getExec(), element.getRetorno());
	}

	private Object execute(Execution exe, String template) throws Exception {
		String func = this.execFuncTemplate.replace(JavaScript.IDENTIFICADOR_TEMPLATE, "'" + exe.identificador + "'")
				.replace(JavaScript.FUNCTION_TEMPLATE, template);
		this.executions.put(exe.identificador, exe);
		exe.browser.executeJavaScript(func, exe.browser.getURL(), 0);
		Thread.sleep(100);
		this.waitFor(exe);

		this.executions.remove(exe.identificador);

		if (exe.step.equals(ExecutionStep.ERROR))
			throw (Exception) exe.value;
		return exe.value;
	}

	public <T> T facade(Class<T> c, String name, CefBrowser browser) {
		try {
			if (SerranoJS.class.isAssignableFrom(c))
				return (T) initSerranoJS(browser);
			if (!c.isInterface()&& !JSElement.class.isAssignableFrom(c)) {
				throw new IllegalArgumentException("O tipo fornecido não é uma interface: " + c.getName());
			}

			var exec = new Execution();
			exec.step = ExecutionStep.NEW;
			exec.returnType = c;
			exec.identificador = "__FACADE" + UUID.randomUUID().toString() + "FACADE__";
			exec.js = this;

			exec.serranoJs = this.initSerranoJS(browser);
			exec.browser = browser;

			return (T) this.execute(exec, name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private String funcCall(Object o, Execution exe) {
		var m = InstanceUtil.getNotDefaultMethod(o.getClass());
		var callBack = new CallBack();

		var callbackUUID = "__CALLBACK" + UUID.randomUUID().toString() + "CALLBACK__";
		callBack.identificador = callbackUUID;
		callBack.method = m.get();
		callBack.instance = o;
		callBack.execution = exe;
		callBack.step = ExecutionStep.NEW;
		this.callBacks.put(callbackUUID, callBack);
		return this.functionCallBackTemplate.replace(IDENTIFICADOR_TEMPLATE, "'" + callbackUUID + "'");
	}

	private Class<?> getElementClassType(JSElement element) {
		return switch (element.getType()) {
		case DATE -> JSDate.class;
		case NUMBER -> JSNumber.class;
		case OBJECT -> JSObject.class;
		case STRING -> JSString.class;
		case BOOLEAN -> JSBoolean.class;
		case FUNCAO -> JSBoolean.class;
		case UNDEFINED -> JSUndefined.class;
		case ARRAY -> JSArray.class;

		};
	}

	private SerranoJS initSerranoJS(CefBrowser browser) throws NoSuchMethodException, SecurityException {
		if (serranoJSInstances.containsKey(browser))
			return serranoJSInstances.get(browser);

		var instance = (SerranoJS) Proxy.newProxyInstance(SerranoJS.class.getClassLoader(),
				new Class[] { SerranoJS.class },
				(proxy, method, args) -> this.serranoJSInvoke(proxy, method, args, browser));

		this.serranoJSInstances.put(browser, instance);
		return instance;

	}

	public void injectScript(String script, CefBrowser browser) {
		script = this.scriptInjectionTemplate.replace(JavaScript.SCRIPT_TEMPLATE, script);
		browser.executeJavaScript(script, browser.getURL(), 0);
	}

	synchronized void resolve(JSRetorno jsRetorno, Boolean hasError) {
		var execution = executions.get(jsRetorno.getIdentificador());
		synchronized (execution) {
			try {
				execution.value = resolveValue(execution, jsRetorno, hasError);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				execution.value = e;
				hasError = true;
			}
			execution.step = hasError ? ExecutionStep.ERROR : ExecutionStep.FINISHED;
			execution.notifyAll();
		}

	}

	CallBack resolveCallBack(String id, List<JSRetorno> jsRetornos) throws Exception {
		CallBack callback = this.callBacks.get(id);
		Parameter[] methodParams = callback.method.getParameters();
		Object[] params = new Object[methodParams.length];
		for (int i = 0; i < methodParams.length; i++) {
			params[i] = jsRetornos.size() <= i ? null
					: createInstance(callback.execution, jsRetornos.get(i), methodParams[i].getParameterizedType());
		}

		Boolean hasError = false;
		Object retorno = null;
		try {
			callback.method.setAccessible(true);
			retorno = callback.method.invoke(callback.instance, params);
		} catch (Exception e) {
			retorno = e;
			hasError = true;
			e.printStackTrace();
		}

		for (var jsRetorno : jsRetornos)
			callback.execution.serranoJs.finalizeObject(jsRetorno.getIdentificador());
		callback.step = hasError ? ExecutionStep.ERROR : ExecutionStep.FINISHED;
		callback.value = retorno;
		return callback;
	}

	public Object resolveInstance(JSElement element, Type type) throws Exception {

		Class<?> returnType = JSTypeResolver.getType(type);

		if (Void.class.equals(returnType))
			return null;
		if (element instanceof JSUndefined undefined)
			return undefined.get();

		if (JSElement.class.isAssignableFrom(returnType))
			return element;

		if (!returnType.isInterface())
			return createPrimitive(element);

		if (!element.getType().equals(JSElementType.OBJECT) && !element.getType().equals(JSElementType.FUNCAO)
				&& !element.getType().equals(JSElementType.ARRAY))
			return element;

		if (element.getType().equals(JSElementType.FUNCAO) && !InstanceUtil.isFunctionalInterface(returnType))
			throw new Exception("Não é uma interface funcional" + returnType);

		return createProxyInstance(returnType, element);

	}

	private Object resolveValue(Execution exe, JSRetorno jsRetorno, Boolean hasError) throws Exception {
		if (hasError)
			throw new Exception(jsRetorno.getValue().getAsString());

		return createInstance(exe, jsRetorno, exe.returnType);

	}

	private Object serranoJSInvoke(Object proxy, Method method, Object[] args, CefBrowser browser) throws Exception {
		String id = "__SERRANOCALL" + UUID.randomUUID().toString() + "SERRANOCALL__";
		var exec = new Execution();
		exec.step = ExecutionStep.NEW;

		exec.returnType = method.getGenericReturnType();
		exec.identificador = id;
		exec.js = this;
		exec.serranoJs = this.initSerranoJS(browser);
		exec.browser = browser;

		String request = "serrano." + method.getName() + "(" + createParameters(exec, args) + ");";

		return this.execute(exec, request);
	}

	private void waitFor(Execution exe) {
		synchronized (exe) {
			try {

				if (!ExecutionStep.NEW.equals(exe.step))
					return;

				exe.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	public class CallBack {
		public Object value;
		transient public Execution execution;
		transient Method method;
		transient Object instance;
		String identificador;
		ExecutionStep step;
	}
	public static class Execution {
		public CefBrowser browser;
		public SerranoJS serranoJs;
		public JavaScript js;
		public volatile ExecutionStep step;
		public volatile Object value;
		public Type returnType;
		public String identificador;
	}
	public static enum ExecutionStep {
		NEW, FINISHED, ERROR
	}

}
