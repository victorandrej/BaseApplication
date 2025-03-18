package base.gson.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

import io.github.victorandrej.tinyioc.steriotypes.Bean;

@Bean
public class ExceptionAdapter extends TypeAdapter<Exception> {

    @Override
    public void write(JsonWriter out, Exception exception) throws IOException {
        if (exception == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("message").value(exception.getMessage());
        out.name("class").value(exception.getClass().getName());
        out.name("stackTrace").beginArray();
        for (StackTraceElement element : exception.getStackTrace()) {
            out.value(element.toString());
        }
        out.endArray();
        out.endObject();
    }


    @Override
    public Exception read(JsonReader in) throws IOException {
    	throw new RuntimeException("NAO IMPLEMENTADO");
    }
}
