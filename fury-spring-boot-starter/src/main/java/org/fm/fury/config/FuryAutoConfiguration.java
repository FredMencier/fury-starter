package org.fm.fury.config;

import org.apache.fury.Fury;
import org.apache.fury.config.CompatibleMode;
import org.apache.fury.config.Language;
import org.fm.fury.annotation.FuryObject;
import org.fm.fury.FuryConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Set;

import static org.fm.fury.config.FuryProperties.SCAN_PACKAGES_KEY;
import static org.fm.fury.config.FuryProperties.WITH_LANGUAGE_KEY;

@AutoConfiguration
@ConditionalOnClass(Fury.class)
@EnableConfigurationProperties(FuryProperties.class)
public class FuryAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(FuryAutoConfiguration.class);

    private final FuryProperties furyProperties;

    public FuryAutoConfiguration(FuryProperties furyProperties) {
        this.furyProperties = furyProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public FuryConfig furyConfig() {

        String withLanguage = furyProperties.withLanguage() == null
                ? Language.JAVA.name()
                : furyProperties.withLanguage();

        String[] scanPackages = furyProperties.scanPackages() == null
                ? new String[0]
                : furyProperties.scanPackages();

        FuryConfig furyConfig = new FuryConfig();
        furyConfig.put(WITH_LANGUAGE_KEY, withLanguage);
        furyConfig.put(SCAN_PACKAGES_KEY, scanPackages);

        return furyConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public Fury fury(FuryConfig furyConfig) {
        Fury fury = Fury.builder()
                .withLanguage(Language.valueOf((String) furyConfig.get(WITH_LANGUAGE_KEY)))
                .withAsyncCompilation(true)
                .withCompatibleMode(CompatibleMode.COMPATIBLE)
                .build();

        String[] scanPackages = (String[]) furyConfig.get(SCAN_PACKAGES_KEY);
        if (scanPackages != null && scanPackages.length > 0) {
            for (String scanPackage : scanPackages) {
                Reflections reflections = new Reflections(scanPackage);
                Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(FuryObject.class);
                allClasses.forEach(aClass -> {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Register class %s as furyObject".formatted(aClass.getName()));
                    }
                    fury.register(aClass);
                });
            }
        } else {
            LOG.error("scanPackages property must be defined to register fury objects");
        }
        return fury;
    }
}
