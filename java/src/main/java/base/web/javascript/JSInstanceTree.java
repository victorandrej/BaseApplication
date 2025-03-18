package base.web.javascript;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import base.web.SerranoJS;

public class JSInstanceTree {
	private static Map<String, Instance> instances = Collections.synchronizedMap(new HashMap());

	public static void put(String parent, String id, SerranoJS js) {
		if(instances.containsKey(id))
			return;
		Instance in = new Instance();
		in.id = id;
		in.parent = instances.get(parent);
		in.serrJS = js;
		instances.put(id, in);
	}

	public static void delete(String id) {
		var in = instances.get(id);
		in.count--;
		if (in.count <= 0)
			delete(in);
		if (Objects.nonNull(in.parent)) {
			delete(in.parent.id);
		}
	}

	private static void delete(Instance in) {
		in.serrJS.finalizeObject(in.id);
		instances.remove(in.id);
	}

	static class Instance {
		volatile int count = 1;
		String id;
		Instance parent;
		SerranoJS serrJS;
	}
}
