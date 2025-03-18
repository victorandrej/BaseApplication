package base.util;

import java.io.File;

import io.github.victorandrej.tinyioc.config.Configuration;
import io.github.victorandrej.tinyioc.order.Ring0;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import io.github.victorandrej.tinyioc.steriotypes.BeanFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import base.gson.adapter.ClassTypeAdapter;
import base.gson.adapter.ExceptionAdapter;
import base.gson.adapter.FileAdapter;
import base.gson.adapter.JSElementAdapter;
import base.gson.adapter.VoidAdapter;
import base.web.javascript.types.JSElement;

@Bean(priority = Ring0.class)
public class GsonConfiguration implements BeanFactory {


	private  final  FileAdapter fileAdapter;

  private  final VoidAdapter voidAdapter;


  private  final JSElementAdapter jsElementAdapter;

  private  final ExceptionAdapter exceptionAdapter;
  private  final String datePattern;

  public GsonConfiguration(FileAdapter fileAdapter,VoidAdapter voidAdapter, JSElementAdapter jsElementAdapter, ExceptionAdapter exceptionAdapter,
                           org.apache.commons.configuration2.Configuration  configuration){
    this.fileAdapter = fileAdapter;
    this.voidAdapter =voidAdapter;
    this.jsElementAdapter =jsElementAdapter;
    this.exceptionAdapter = exceptionAdapter;
    this.datePattern = configuration.getString("serrano.date.pattern");
  }

	public Gson gson() {

		return new GsonBuilder().registerTypeAdapter(File.class, fileAdapter)
				.registerTypeAdapter(JSElement.class, jsElementAdapter)


				.registerTypeAdapterFactory(new TypeAdapterFactory() {

					@Override
					public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
						if (!Exception.class.isAssignableFrom(type.getRawType())) {
							return null;
						}
						return (TypeAdapter<T>) exceptionAdapter;
					}
				})
				.registerTypeAdapterFactory(new TypeAdapterFactory() {

					@Override
					public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
						if (!Class.class.isAssignableFrom(type.getRawType())) {
							return null;
						}
						return (TypeAdapter<T>) new ClassTypeAdapter();
					}
				}).registerTypeAdapter(void.class, voidAdapter).setDateFormat(datePattern).create();
	}

   @Override
   public void create(Configuration configuration) throws Exception {
     configuration.bean(gson(),"gson", Ring0.class);
   }
 }
