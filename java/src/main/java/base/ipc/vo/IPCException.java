package base.ipc.vo;

import base.exception.SerranoException;

import jakarta.validation.ConstraintViolationException;

public class IPCException{
	private String className;
	private String message;
	private String code;

	public IPCException(Throwable exception) {
		this.className = exception.getClass().getName();
		this.message = getMessage(exception);
		if (exception instanceof SerranoException) {
			SerranoException serranoException = (SerranoException) exception;
			code = serranoException.getCodigo();
		}
	}

	private String getMessage(Throwable t) {
		if (t instanceof ConstraintViolationException) {
			return ((ConstraintViolationException) t).getConstraintViolations().stream().map(c -> c.getMessage())
					.reduce("", (s1, s2) -> s1 + "\n" + s2);
		}

		return t.getMessage();
	}

	public String getClassName() {
		return className;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return this.code;

	}
}
