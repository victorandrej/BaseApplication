package base.properties;

import base.util.ResourceUtils;

import io.github.victorandrej.tinyioc.config.Configuration;

import io.github.victorandrej.tinyioc.order.Ring0;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import io.github.victorandrej.tinyioc.steriotypes.BeanFactory;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;

@Bean(priority = Ring0.class)
public class PropertyFactory implements BeanFactory {

  @Override
  public void create(Configuration configuration) throws Exception {
    ResourceUtils.getResourceFiles("application.yml").execute((r) -> {
      YAMLConfiguration config = new YAMLConfiguration();
      config.read(r.getInputStream());
      ConfigurationInterpolator interpolator = config.getInterpolator();

      interpolator.addDefaultLookup(key -> System.getProperty(key));


      interpolator.addDefaultLookup(key -> {

        if (config.containsKey(key)) {
          return config.getString(key);
        }
        return null;
      });
      configuration.bean(config, "yAMLConfiguration",Ring0.class);

    });
  }
}
