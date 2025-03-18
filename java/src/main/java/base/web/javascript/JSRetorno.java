package base.web.javascript;

import com.google.gson.JsonElement;
import base.web.javascript.types.JSElementType;

public class JSRetorno {
	private JSElementType type;
	private JsonElement value;
	private String identificador;

	public JSRetorno(JSElementType type, JsonElement value, String identificador) {
		super();
		this.type = type;
		this.value = value;
		this.identificador = identificador;
	}

	public JSElementType getType() {
		return type;
	}

	public JsonElement getValue() {
		return value;
	}

	public String getIdentificador() {
		return identificador;
	}

}
