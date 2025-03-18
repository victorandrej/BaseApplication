package base.exception;

public class SerranoException extends Exception {

	private String codigo;

	public SerranoException(String codigo, String message) {
		super(message);
		this.codigo = codigo;
	}

	public SerranoException(String codigo, Exception e) {
		super(e);
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

}
