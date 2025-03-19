package base.gson.adapter;

import java.io.IOException;
import java.text.SimpleDateFormat;

import io.github.victorandrej.tinyioc.IOC;
import io.github.victorandrej.tinyioc.steriotypes.Bean;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import base.Application;
import base.web.SerranoJS;
import base.web.javascript.JavaScript;
import base.web.javascript.types.JSBoolean;
import base.web.javascript.types.JSDate;
import base.web.javascript.types.JSElement;
import base.web.javascript.types.JSNumber;
import base.web.javascript.types.JSPrimitive;
import base.web.javascript.types.JSString;


import org.apache.commons.configuration2.Configuration;

@Bean
public class JSElementAdapter extends TypeAdapter<JSElement> {

	JavaScript js;

	Gson gson;

  IOC ioc;

	SimpleDateFormat simpleDateFormat;

	public JSElementAdapter(Configuration configuration, IOC ioc) {
		simpleDateFormat = new SimpleDateFormat(configuration.getString("date.pattern"));
    this.ioc = ioc;
    init();
	}


	public void init() {
		Application.onStart(() -> {
      this.gson = ioc.getInstance(Gson.class);
      this.js = ioc.getInstance(JavaScript.class);

		});
	}

	@Override
	public void write(JsonWriter out, JSElement value) throws IOException {
		var contexto = value.getContexto();
		if (value instanceof JSPrimitive p) {

			switch (p.getType()) {
			case BOOLEAN -> out.value(((JSBoolean) p).get());
			case DATE -> out.value(simpleDateFormat.format(((JSDate) p).get()));
			case NUMBER -> out.value(((JSNumber) p).get());
			case STRING -> out.value(((JSString) p).get());
			case UNDEFINED -> out.nullValue();
			default -> throw new UnsupportedOperationException();
			}

		} else {

			SerranoJS serrJS = js.facade(SerranoJS.class, "serrano", contexto);

			String objString = serrJS.objectToString(value.getExec().identificador);

			out.jsonValue(objString);
		}

	}

	@Override
	public JSElement read(JsonReader in) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
