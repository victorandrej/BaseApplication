package base;

import base.properties.PropertyFactory;
import base.web.javascript.JavaScript;
import io.github.victorandrej.tinyioc.IOC;
import io.github.victorandrej.tinyioc.config.Configuration;
import io.github.victorandrej.tinyioc.order.BeanOrder;
import io.github.victorandrej.tinyioc.order.Ring0;
import io.github.victorandrej.tinyioc.steriotypes.Bean;
import io.github.victorandrej.tinyioc.steriotypes.BeanFactory;

@Bean(order = BeanOrder.AFTER, classOrder = PropertyFactory.class,priority = Ring0.class)
public class WindowFactory implements BeanFactory {
  private final org.apache.commons.configuration2.Configuration configuration;
  private final JavaScript js;
  private IOC ioc ;

  public WindowFactory(org.apache.commons.configuration2.Configuration configuration, JavaScript js, IOC ioc) {
    this.configuration = configuration;
    this.js = js;
    this.ioc = ioc;
  }

  @Override
  public void create(Configuration configuration) throws Exception {

    Window window = new Window(this.configuration.getString("serrano.view.folder"),ioc)
      ;
    Application.window = window;


    configuration.bean(window, "serranoWindow", Ring0.class);
    window.open();

  }
}
