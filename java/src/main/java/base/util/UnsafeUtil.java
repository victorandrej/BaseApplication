package base.util;

import java.lang.reflect.Field;

import javax.management.RuntimeErrorException;
import sun.misc.Unsafe;
public class UnsafeUtil {
	private static final Unsafe UNSAFE ;

	static {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			UNSAFE = (Unsafe) f.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
}
