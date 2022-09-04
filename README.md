# About
AbaseCode open source project is a set of open source collection . Including the base package , toolkit , security package , token package , payment package , excel package and so on.

Open source project components to do out of the box, to facilitate more developers to save duplication of work, more focused on business logic code writing.

I am Jon, a developer who focuses on learning and spreading technical knowledge. I hope these toolkits can help you, and welcome any friends to join this open source project.

project homepage : https://abasecode.com

project github : https://github.com/abasecode

Jon's blog : https://jon.wiki

e-mail: ijonso123@gmail.com

# About abasecode-base-token
A token base library, based on shiro to achieve login verification.


# Quick Start
## Step 1: setting the pom.xml add dependency
``` xml
<dependency>
    <groupId>com.abasecode.opencode</groupId>
    <artifactId>abasecode-base-token</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Step 2: setting application.yaml
``` yaml
app:
  token:
    key: String format. the key in redis.
    secret: String format. the token secret. example: "3d15d32654bc1af61759a3bacbc0c78a"
    expire: Integer format. the token expire time (seconds).   
```

## Step 3: create ShiroConfig.java in your project
Note that the shiro function can only be used in the full mode.
```java
import com.abasecode.opencode.base.token.auth.AuthFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.context.annotation.Bean;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        filters.put("oauth2", new AuthFilter());
        shiroFilter.setFilters(filters);
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/login", "anon");
        filterMap.put("/**", "oauth2");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }
}
```
## Step 4: add annotation
```java
@EnableCodeToken
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
```

## Step 5: No more step. enjoy it.

## Notice 1: redis need.
This component uses redis to access related authorization information. Redis must be introduced in the SpringBoot project.
``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
## Notice 2: app.token.key and simple mode.
The default value of app.token.key is "adm-token".
simple mode: If you don't use shiro's authorization and verification functions, you can use the simple mode.
Simple mode allows you to use multiple custom keys in a project.

### simple mode example.
``` java
    @Autowired
    TokenHandler tokenHandler;
    
    ...
    
    Token token = tokenHandler.createToken(user);

```