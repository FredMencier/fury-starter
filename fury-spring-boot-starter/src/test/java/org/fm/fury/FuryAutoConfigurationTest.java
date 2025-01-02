package org.fm.fury;

import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.fm.fury.config.FuryAutoConfiguration;
import org.fm.fury.config.FuryProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class FuryAutoConfigurationTest {

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("org.fury.scanPackages=org.test1,org.test2");

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
}
