package org.fm.fury;

import org.apache.fury.BaseFury;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;
import org.apache.fury.pool.ThreadPoolFury;
import org.apache.fury.resolver.ClassResolver;
import org.fm.dto.MyObject;
import org.fm.dto.MyObjectWithClassId;
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

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER_THREADSAFE = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("springboot.fury.scanPackages=org.fm.dto,org.test2");

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER_THREADSAFEPOOL = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("springboot.fury.scanPackages=org.fm.dto,org.test2", "springboot.fury.treadSafeFuryPool=true");

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER_THREADSAFE_BAD_OBJECT = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("springboot.fury.scanPackages=org.fm.obj");

    private static final ApplicationContextRunner APPLICATION_CONTEXT_RUNNER_NOT_THREADSAFE = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FuryAutoConfiguration.class))
            .withPropertyValues("springboot.fury.scanPackages=org.fm.dto,org.test2", "springboot.fury.threadSafe=false");

    @Test
    public void shouldContainFuryBeans() {
        APPLICATION_CONTEXT_RUNNER_THREADSAFE.run(context -> {
            Assertions.assertTrue(context.containsBean("fury"));
            Assertions.assertTrue(context.containsBean("furyConfig"));
        });
    }

    @Test
    public void shouldContainFuryConfigLanguage() {
        APPLICATION_CONTEXT_RUNNER_THREADSAFE.run(context -> {
            BaseFury fury = (BaseFury) context.getBean("fury");
            FuryConfig furyConfig = (FuryConfig) context.getBean("furyConfig");
            Assertions.assertNotNull(Language.valueOf((String) furyConfig.get(FuryProperties.WITH_LANGUAGE_KEY)));
            Assertions.assertNotNull(fury);
        });
    }

    @Test
    public void shouldContainFuryConfigScanPackages() {
        APPLICATION_CONTEXT_RUNNER_THREADSAFE.run(context -> {
            FuryConfig furyConfig = (FuryConfig) context.getBean("furyConfig");
            String[] scanPackages = (String[]) furyConfig.get(FuryProperties.SCAN_PACKAGES_KEY);
            Assertions.assertNotNull(scanPackages);
            Assertions.assertEquals(2, scanPackages.length);
        });
    }

    @Test
    public void shouldRegisterMyObjectThreadsafe() {
        APPLICATION_CONTEXT_RUNNER_THREADSAFE.run(context -> {
            BaseFury fury = (BaseFury) context.getBean("fury");
            Assertions.assertInstanceOf(ThreadSafeFury.class, fury);
            List<Class<?>> registeredClasses = getClassResolver(fury).getRegisteredClasses().stream().filter(aClass -> aClass.isAnnotationPresent(FuryObject.class)).toList();
            Assertions.assertEquals(2, registeredClasses.size());
            Assertions.assertTrue(registeredClasses.stream().anyMatch(aClass -> MyObject.class.getName().equals(aClass.getName())));
            Assertions.assertTrue(registeredClasses.stream().anyMatch(aClass -> MyObjectWithClassId.class.getName().equals(aClass.getName())));
        });
    }

    @Test
    public void shouldRegisterMyObjectThreadsafePool() {
        APPLICATION_CONTEXT_RUNNER_THREADSAFEPOOL.run(context -> {
            BaseFury fury = (BaseFury) context.getBean("fury");
            Assertions.assertInstanceOf(ThreadPoolFury.class, fury);
            List<Class<?>> registeredClasses = getClassResolver(fury).getRegisteredClasses().stream().filter(aClass -> aClass.isAnnotationPresent(FuryObject.class)).toList();
            Assertions.assertEquals(2, registeredClasses.size());
            Assertions.assertTrue(registeredClasses.stream().anyMatch(aClass -> MyObject.class.getName().equals(aClass.getName())));
            Assertions.assertTrue(registeredClasses.stream().anyMatch(aClass -> MyObjectWithClassId.class.getName().equals(aClass.getName())));
        });
    }

    @Test
    public void shouldRegisterMyObject() {
        APPLICATION_CONTEXT_RUNNER_NOT_THREADSAFE.run(context -> {
            Fury fury = (Fury) context.getBean("fury");
            List<Class<?>> registeredClasses = fury.getClassResolver().getRegisteredClasses().stream().filter(aClass -> aClass.isAnnotationPresent(FuryObject.class)).toList();
            Assertions.assertEquals(2, registeredClasses.size());
            Assertions.assertTrue(registeredClasses.stream().anyMatch(aClass -> MyObject.class.getName().equals(aClass.getName())));
            Assertions.assertTrue(registeredClasses.stream().anyMatch(aClass -> MyObjectWithClassId.class.getName().equals(aClass.getName())));
        });
    }

    @Test
    public void shouldRegisterMyObjectWithClassId() {
        APPLICATION_CONTEXT_RUNNER_NOT_THREADSAFE.run(context -> {
            Fury fury = (Fury) context.getBean("fury");
            Class<?> registeredClass = fury.getClassResolver().getRegisteredClass(Short.parseShort("1000"));
            Assertions.assertNotNull(registeredClass);
        });
    }

    @Test
    public void shouldThrowExceptionWhenRegister() {
        assertThrows(
                IllegalStateException.class,
                () -> APPLICATION_CONTEXT_RUNNER_THREADSAFE_BAD_OBJECT.run(context -> context.getBean("fury")),
                "Expected to throw IllegalStateException"
        );
    }

    private ClassResolver getClassResolver(BaseFury fury) {
        if (fury instanceof ThreadSafeFury threadSafeFury) {
            return threadSafeFury.execute(Fury::getClassResolver);
        } else {
            return ((Fury) fury).getClassResolver();
        }
    }
}
