## Reference Documentation
- Use annotation **@FuryObject** to discover and register objects

- Configuration :

  | Property | Mandatory | Default value | Description         |
  |----------|-----------|---------------|---------------------|
  | **springboot.fury.withLanguage** | false     | JAVA          | define fury language|
  | **springboot.fury.scanPackages** | true      | none          | list package to scan for objects registration |
  | **springboot.fury.compressInt** | false     | true          | see https://fury.apache.org/docs/guide/java_object_graph_guide |
  | **springboot.fury.compressLong** | false     | true          | see https://fury.apache.org/docs/guide/java_object_graph_guide |
  | **springboot.fury.compressString** | false     | false         | see https://fury.apache.org/docs/guide/java_object_graph_guide |
  | **springboot.fury.asyncCompilation** | false     | false         | see https://fury.apache.org/docs/guide/java_object_graph_guide |
  | **springboot.fury.classRegistration** | false     | true          | see https://fury.apache.org/docs/guide/java_object_graph_guide |
  | **springboot.fury.threadSafe** | false     | true          | use threadsafe fury bean serializer/deserializer with pool of 2 to 10 beans |
  |          |           |               |                     |

- Use mediaType **fury** or ***+fury** (FuryMediaType) to use fury serialization

## Getting Started

Add starter to your project
```xml
    <dependency>
        <groupId>org.fm.fury</groupId>
        <artifactId>fury-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
```

Annotate object you want register
```java
@FuryObject
public class AccountDto {
}
```

You can specify classId if you do not want an auto-generated fury classId
classId must be >= 256 and <= Short.MAX_VALUE
```java
@FuryObject(classId = 1000)
public class AccountDto {
}
```

Configure package you want to scan to help starter to find fury objects
```properties
#Fury Starter
springboot.fury.scanPackages=org.fm,org.test
```

Use MediaType Fury in your controller to use communication with fury serialized objects
```java
@GetMapping(path = "/search", produces = {FuryMediaType.APPLICATION_FURY_VALUE})
public AccountDto getAccount(@RequestParam String accountNo) {
    return accountManager.findByAccountNo(accountNo);
}
```

A springboot demo project using this starter can be found at https://github.com/FredMencier/spring3-demo-fury