package org.fm.fury.config;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.config.CompatibleMode;
import org.apache.fury.config.FuryBuilder;
import org.apache.fury.config.Language;
import org.apache.fury.util.Preconditions;
import org.fm.fury.FuryConfig;
import org.fm.fury.annotation.FuryObject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Set;

import static org.fm.fury.config.FuryProperties.*;

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

        String[] scanPackages = furyProperties.scanPackages() == null
                ? new String[0]
                : furyProperties.scanPackages();

        FuryConfig furyConfig = new FuryConfig();
        furyConfig.put(WITH_LANGUAGE_KEY, furyProperties.withLanguage());
        furyConfig.put(SCAN_PACKAGES_KEY, scanPackages);
        furyConfig.put(COMPRESS_INT_KEY, furyProperties.compressInt());
        furyConfig.put(COMPRESS_LONG_KEY, furyProperties.compressLong());
        furyConfig.put(COMPRESS_STRING_KEY, furyProperties.compressString());
        furyConfig.put(ASYNC_COMPILATION_KEY, furyProperties.asyncCompilation());
        furyConfig.put(CLASS_REGISTRATION_KEY, furyProperties.classRegistration());
        furyConfig.put(THREAD_SAFE_KEY, furyProperties.threadSafe());

        return furyConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public BaseFury fury(FuryConfig furyConfig) {
        boolean useThreadSafe = (boolean) furyConfig.get(THREAD_SAFE_KEY);
        FuryBuilder furyBuilder = Fury.builder()
                .withLanguage(Language.valueOf((String) furyConfig.get(WITH_LANGUAGE_KEY)))
                .requireClassRegistration((boolean) furyConfig.get(CLASS_REGISTRATION_KEY))
                .withIntCompressed((boolean) furyConfig.get(COMPRESS_INT_KEY))
                .withLongCompressed((boolean) furyConfig.get(COMPRESS_LONG_KEY))
                .withStringCompressed((boolean) furyConfig.get(COMPRESS_STRING_KEY))
                .withAsyncCompilation((boolean) furyConfig.get(ASYNC_COMPILATION_KEY))
                .withCompatibleMode(CompatibleMode.COMPATIBLE);
        BaseFury fury;
        if (useThreadSafe) {
            fury = furyBuilder.buildThreadSafeFuryPool(2, 10);
        } else {
            fury = furyBuilder.build();
        }

        String[] scanPackages = (String[]) furyConfig.get(SCAN_PACKAGES_KEY);
        if (scanPackages != null && scanPackages.length > 0) {
            for (String scanPackage : scanPackages) {
                Reflections reflections = new Reflections(scanPackage);
                Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(FuryObject.class);
                allClasses.forEach(aClass -> {
                    FuryObject annotation = aClass.getAnnotation(FuryObject.class);
                    short classId = annotation.classId();
                    if (classId == 0) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Register class %s as furyObject with auto-generated id".formatted(aClass.getName()));
                        }
                        fury.register(aClass);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Register class %s as furyObject with id %s".formatted(aClass.getName(), classId));
                        }
                        Preconditions.checkArgument(
                                classId >= 256 && classId <= Short.MAX_VALUE,
                                "classId %s must be >= 256 and <= %s",
                                classId,
                                Short.MAX_VALUE);
                        fury.register(aClass, classId);
                    }
                });
            }
        } else {
            LOG.error("scanPackages property must be defined to register fury objects");
        }
        return fury;
    }
}
