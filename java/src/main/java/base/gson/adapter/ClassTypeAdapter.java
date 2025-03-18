package base.gson.adapter;

import java.io.IOException;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.victorandrej.tinyioc.steriotypes.Bean;

@Bean
public class ClassTypeAdapter extends TypeAdapter<Class<?>> {



	@Override
	public void write(final JsonWriter out, final Class<?> value) throws IOException {

		out.value(value.getName());
	}

	@Override
	public Class<?> read(final JsonReader in) throws IOException {
		try {

			String className = in.nextString();
			return Class.forName(className);
		} catch (final ClassNotFoundException ex) {

			throw new JsonParseException(ex);
		}
	}

}
