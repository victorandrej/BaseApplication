package base.web.javascript;

import java.util.Objects;

import base.Application;
import org.cef.browser.CefBrowser;

public class Funcoes {

	private static JavaScript js;

	static {
		if (Objects.isNull(js))
			js = Application.getIOC().getInstance(JavaScript.class);
		Application.onStart(() -> {
			if (Objects.isNull(js))
				js = Application.getIOC().getInstance(JavaScript.class);
		});

	}

	/**
	 * cria um callback para ser chamado no js
	 *
	 * @param o       instancia de uma interface funcional
	 * @param browser onde sera executado
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static String callback(Object o, CefBrowser browser) throws NoSuchMethodException, SecurityException {
		return js.callback(o, browser);
	}

}
