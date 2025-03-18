package base.ipc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permissao {
	public static String LOGADOS = "*";
	public static String TODOS = "ALL";

	public String[] value();

	public boolean isPromise() default false;
}
