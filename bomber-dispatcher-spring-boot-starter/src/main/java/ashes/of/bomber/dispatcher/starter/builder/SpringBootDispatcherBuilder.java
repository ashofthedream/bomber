package ashes.of.bomber.dispatcher.starter.builder;

import ashes.of.bomber.core.Application;
import ashes.of.bomber.dispatcher.Dispatcher;
import ashes.of.bomber.dispatcher.DispatcherBuilder;
import ashes.of.bomber.dispatcher.starter.config.DispatcherConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBootDispatcherBuilder implements DispatcherBuilder {

    @Override
    public Dispatcher build(Application application) {
        ConfigurableApplicationContext app = new SpringApplicationBuilder()
                .sources(DispatcherConfig.class)
                .initializers(context -> context.getBeanFactory().registerResolvableDependency(Application.class, application))
                .run();

        return app.getBean(Dispatcher.class);
    }
}
