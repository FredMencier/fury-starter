## Reference Documentation
- Use annotation @FuryObject to discover and register objects
- Configuration :
    - **org.fury.withLanguage** to define fury language
    - **org.fury.scanPackages** list package to scan for objects registration
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

Configure package you want to scan to help starter to find fury objects
```properties
#Fury Starter
org.fury.scanPackages=org.heg
```

Use MediaType Fury in your controller to use communication with fury serialized objects
```java
@GetMapping(path = "/search", produces = {FuryMediaType.APPLICATION_FURY_VALUE})
public AccountDto getAccount(@RequestParam String accountNo) {
    return accountManager.findByAccountNo(accountNo);
}
```