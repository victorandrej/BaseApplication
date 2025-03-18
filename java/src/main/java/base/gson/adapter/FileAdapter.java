package base.gson.adapter;

import java.io.File;
import java.io.IOException;

import io.github.victorandrej.tinyioc.steriotypes.Bean;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

@Bean
public class FileAdapter extends TypeAdapter<File> {

	@Override
	public void write(JsonWriter out, File value) throws IOException {

		throw new IOException("Não implementado");
	}



	@Override
	public File read(JsonReader in) throws IOException {

		return new File(in.nextString());

	}

}
