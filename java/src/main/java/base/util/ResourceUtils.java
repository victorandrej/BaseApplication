package base.util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceUtils {

	static File JARPATH;

	static {
		JARPATH = FileUtils.getJarLocation().toFile();
	}

	private static Resource getResourceFilesDev2(Path path) throws IOException {
		File filePath = path.toFile();

		if (filePath.isFile())
			return new Resource(filePath.getName(), path, false, new FileInputStream(filePath).readAllBytes(),
					new ArrayList());

		var resource = new Resource(filePath.getName(), path, true, null, new ArrayList());

		for (var file : filePath.listFiles()) {
			resource.getResources().add(getResourceFilesDev2(file.toPath()));
		}
		return resource;
	}

	private static Resource getResourceFilesDev(String pathString) throws IOException {
		Path path = JARPATH.toPath().resolve(pathString);
		return getResourceFilesDev2(path);
	}

	public static ResourceExecutor getResourceFiles(String path) throws IOException {

		if (!JARPATH.isFile())
			return new ResourceExecutor(getResourceFilesDev(path));

		final Path findedPath = Paths.get(path);

		try (ZipFile zipFile = new ZipFile(JARPATH)) {

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			List<Resource> resources = new ArrayList();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				Path entryPath = Paths.get(entry.getName());

				if (!entryPath.startsWith(findedPath))
					continue;
				String name;

				if (entry.isDirectory()) {
					name = entry.getName().substring(0, entry.getName().length() - 1);
					name = name.substring(name.lastIndexOf("/") + 1);
				} else {
					name = entry.getName();
					name = name.substring(name.lastIndexOf("/") + 1);
				}
				InputStream zipInput = zipFile.getInputStream(entry);

				Resource r = new Resource(name, entryPath, entry.isDirectory(),
						entry.isDirectory() ? null : zipInput.readAllBytes(), new ArrayList());
				resources.add(r);

				String parentPathString;

				if (entry.isDirectory()) {

					parentPathString = entry.getName().substring(0, entry.getName().length() - 1);
					var indexParent = parentPathString.lastIndexOf("/");
					if (indexParent < 0)
						continue;

					parentPathString = parentPathString.substring(0, indexParent);
				} else {
					parentPathString = entry.getName();
					var indexParent = parentPathString.lastIndexOf("/");

					if (indexParent < 0)
						continue;

					parentPathString = parentPathString.substring(0, indexParent);
				}

				Path parentPath = Paths.get(parentPathString);

				resources.stream().filter(re -> re.getPath().compareTo(parentPath) == 0).findFirst()
						.ifPresent(re -> re.getResources().add(r));

			}
			if (resources.isEmpty())
				throw new IOException(path + " NAO EXISTE");
			return new ResourceExecutor(
					resources.stream().filter(re -> re.getPath().compareTo(findedPath) == 0).findFirst().orElse(null));
		}
	}

	public static class ResourceExecutor {
		private Resource r;
		private List<Resource> allResources;

		protected ResourceExecutor(Resource r) {
			this.r = r;
			this.allResources = new ArrayList();
			getAllResourcers(r, this.allResources);
		}

		private void getAllResourcers(Resource r2, List<Resource> resources) {
			if (Objects.isNull(r2))
				return;
			resources.add(r2);

			if (Objects.nonNull(r2.getResources()))
				r2.getResources().forEach(r3 -> getAllResourcers(r3, resources));
		}

		private void closeAll() {
			this.allResources.forEach(r -> {
				try {
					if(!r.isFolder())
						r.getInputStream().close();
				} catch (IOException e) {
				}
			});
		}

		public void execute(ResourceExecutorInterface c, Boolean autoClose) throws Exception {
			try {

				c.accept(r);

			} finally {
				if (autoClose)
					closeAll();
			}

		};

		public void execute(ResourceExecutorInterface c) throws Exception {
			execute(c, true);

		};

	}

	public static interface ResourceExecutorInterface {
		public void accept(Resource r) throws Exception;
	}

	public static class Resource {
		private String name;
		private Boolean isFolder;
		private byte[] bytes;
		private List<Resource> resources;
		private Path path;

		public Resource(String name, Path path, Boolean isFolder, byte[] bytes, List<Resource> resources) {
			this.name = name;
			this.isFolder = isFolder;
			this.resources = resources;
			this.bytes = bytes;
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public Boolean isFolder() {
			return isFolder;
		}

		public InputStream getInputStream() {
			return Objects.isNull(bytes) ? null : new ByteArrayInputStream(this.bytes);
		}

		public List<Resource> getResources() {
			return resources;
		}

		public Path getPath() {
			return path;
		}

	}

}
