package org.fm.fury.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "springboot.fury")
public record FuryProperties(

        @DefaultValue("JAVA")
        String withLanguage,
        String[] scanPackages
) {

    public static String WITH_LANGUAGE_KEY = "WITH_LANGUAGE_KEY";

    public static String SCAN_PACKAGES_KEY = "SCAN_PACKAGES_KEY";
}
