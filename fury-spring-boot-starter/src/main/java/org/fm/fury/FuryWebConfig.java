package org.fm.fury;

import org.apache.fury.Fury;
import org.fm.fury.converter.FuryMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableWebMvc
@AutoConfiguration
public class FuryWebConfig implements WebMvcConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(FuryWebConfig.class);

    private final Fury fury;

    public FuryWebConfig(Fury fury) {
        this.fury = fury;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        LOG.info("Add fury HttpMessageConverter");
        converters.add(new FuryMessageConverter(fury));
    }
}
