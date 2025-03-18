package base.gson.adapter;

import java.io.File;
import java.io.IOException;

import io.github.victorandrej.tinyioc.steriotypes.Bean;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@Bean
public class VoidAdapter  extends TypeAdapter<Void>{

	@Override
	public void write(JsonWriter out, Void value) throws IOException {
		out.value((String)null);

	}

	@Override
	public Void read(JsonReader in) throws IOException {
		return null;
	}

}
