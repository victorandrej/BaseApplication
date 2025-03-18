package base.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import base.Application;

public class FileUtils {

	public static Path getJarLocation() {
		String location = new ResourceUtils().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

		location = location.substring(1);

		try {
			return Paths.get(URLDecoder.decode(location, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
			// nunca vai acontecer
		}

	}

	public static Path getLocation() {
		Path jarLocation = getJarLocation();
		if (Application.IS_DEV_MODE)
			return jarLocation;
		String jarLocationString = getJarLocation().toString();
		String location = jarLocationString.substring(0, jarLocationString.lastIndexOf(File.separator) + 1);
		return Paths.get(location);
	}
}
