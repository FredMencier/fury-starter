package org.fm.fury;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.fm.fury.annotation.FuryObject;
import org.fm.fury.config.FuryAutoConfiguration;
import org.fm.fury.config.FuryProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FuryAutoConfigurationTest {

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("org.fury.scanPackages=org.fm.dto,org.test2");

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER_BAD_OBJECT = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("org.fury.scanPackages=org.fm.obj");

    @Test
    public void shouldContainFuryBeans() {
        APPLICATION_CONTEXT_RUNNER.run(context -> {
            Assertions.assertTrue(context.containsBean("fury"));
            Assertions.assertTrue(context.containsBean("furyConfig"));
        });
    }

    @Test
    public void shouldContainFuryConfigLanguage() {
        APPLICATION_CONTEXT_RUNNER.run(context -> {
            Fury fury = (Fury) context.getBean("fury");
            FuryConfig furyConfig = (FuryConfig) context.getBean("furyConfig");
            Assertions.assertNotNull(Language.valueOf((String) furyConfig.get(FuryProperties.WITH_LANGUAGE_KEY)));
            Assertions.assertNotNull(fury.getConfig());
        });
    }

    @Test
    public void shouldContainFuryConfigScanPackages() {
        APPLICATION_CONTEXT_RUNNER.run(context -> {
            FuryConfig furyConfig = (FuryConfig) context.getBean("furyConfig");
            String[] scanPackages = (String[]) furyConfig.get(FuryProperties.SCAN_PACKAGES_KEY);
            Assertions.assertNotNull(scanPackages);
            Assertions.assertEquals(2, scanPackages.length);
        });
    }

    @Test
    public void shouldRegisterMyObject() {
        APPLICATION_CONTEXT_RUNNER.run(context -> {
            Fury fury = (Fury) context.getBean("fury");
            List<Class<?>> registeredClasses = fury.getClassResolver().getRegisteredClasses().stream().filter(aClass -> aClass.isAnnotationPresent(FuryObject.class)).toList();
            Assertions.assertEquals(2, registeredClasses.size());
        });
    }

    @Test
    public void shouldRegisterMyObjectWithClassId() {
        APPLICATION_CONTEXT_RUNNER.run(context -> {
            Fury fury = (Fury) context.getBean("fury");
            Class<?> registeredClass = fury.getClassResolver().getRegisteredClass(Short.parseShort("1000"));
            Assertions.assertNotNull(registeredClass);
        });
    }

    @Test
    public void shouldThrowExceptionWhenRegister() {
        assertThrows(
                IllegalStateException.class,
                () -> APPLICATION_CONTEXT_RUNNER_BAD_OBJECT.run(context -> context.getBean("fury")),
                "Expected to throw IllegalStateException"
        );
    }
}
