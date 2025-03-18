package base.jcef;

import java.io.IOException;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefSettings;

import base.Application;
import base.util.FileUtils;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;

public final class CefAppInstance {
	private static final CefApp app = createInstance();

	public static CefApp getApp() {
		return app;
	}

	private static CefApp createInstance() {
		CefAppBuilder builder = new CefAppBuilder();
		CefSettings config = builder.getCefSettings();
		 config.windowless_rendering_enabled = false;
		 config.cache_path = FileUtils.getLocation().resolve("serranoCache").toString();

		String[] args = { "--disable-web-security",
				Application.IS_DEV_MODE ? "--remote-debugging-port=9222" : "" };
		builder.addJcefArgs(args);

		builder.setAppHandler(new MavenCefAppHandlerAdapter() {
			@Override
			public void stateHasChanged(org.cef.CefApp.CefAppState state) {

				if (state == CefAppState.TERMINATED)
					System.exit(0);
			}
		});
		try {
			return builder.build();
		} catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
			throw new RuntimeException(e);
		}
	}

	private CefAppInstance() {
	}
}
