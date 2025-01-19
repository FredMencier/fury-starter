package org.fm.fury.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "springboot.fury")
public record FuryProperties(

        @DefaultValue("JAVA")
        String withLanguage,

        @DefaultValue("true")
        boolean compressInt,

        @DefaultValue("true")
        boolean compressLong,

        @DefaultValue("false")
        boolean compressString,

        @DefaultValue("false")
        boolean asyncCompilation,

        @DefaultValue("true")
        boolean classRegistration,

        @DefaultValue("true")
        boolean threadSafe,

        @DefaultValue("false")
        boolean treadSafeFuryPool,

        @DefaultValue("2")
        int minTreadSafeFuryPool,

        @DefaultValue("10")
        int maxTreadSafeFuryPool,

        String[] scanPackages
) {

    public static final String WITH_LANGUAGE_KEY = "WITH_LANGUAGE_KEY";

    public static final String SCAN_PACKAGES_KEY = "SCAN_PACKAGES_KEY";

    public static final String COMPRESS_INT_KEY = "COMPRESS_INT_KEY";

    public static final String COMPRESS_LONG_KEY = "COMPRESS_LONG_KEY";

    public static final String COMPRESS_STRING_KEY = "COMPRESS_STRING_KEY";

    public static final String ASYNC_COMPILATION_KEY = "ASYNC_COMPILATION_KEY";

    public static final String CLASS_REGISTRATION_KEY = "CLASS_REGISTRATION_KEY";

    public static final String THREAD_SAFE_KEY = "THREAD_SAFE_KEY";

    public static final String THREAD_SAFE_FURY_POOL_KEY = "THREAD_SAFE_FURY_POOL_KEY";

    public static final String MIN_THREAD_SAFE_FURY_POOL_KEY = "MIN_THREAD_SAFE_FURY_POOL_KEY";

    public static final String MAX_THREAD_SAFE_FURY_POOL_KEY = "MAX_THREAD_SAFE_FURY_POOL_KEY";
}
