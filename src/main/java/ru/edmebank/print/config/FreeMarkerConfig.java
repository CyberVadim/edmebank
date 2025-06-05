package ru.edmebank.print.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class FreeMarkerConfig {

    @Bean
    public Configuration freemarkerConfig() {
        Configuration config = new Configuration(Configuration.VERSION_2_3_32);
        config.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "templates");
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        return config;
    }
}
