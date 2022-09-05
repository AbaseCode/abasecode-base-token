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
Notice: This component uses redis to access related authorization information. Redis must be introduced in the SpringBoot project.
``` xml
<dependency>
    <groupId>com.abasecode.opencode</groupId>
    <artifactId>abasecode-base-token</artifactId>
    <version>1.0.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## Step 2: setting application.yaml
like this:
app:auth-filter:annos fill the paths that do not require authentication, one per line.
app:auth-filter:auths fill the paths that require authentication, one per line.
``` yaml
app:
  auth-filter:
    annos:
      - /
      - /login
    auths:
      - /user
      - /system
  api-token:
    key: app-name
    expire: 64800
    secret: 1829b4abbba0794301a075fc2283d2ba
```

## Step 3: create ShiroConfig.java in your project
Note that the shiro function can only be used in the full mode.
```java
import com.abasecode.opencode.base.token.annotation.EnableCodeToken;
import com.abasecode.opencode.base.token.auth.AuthFilter;
import com.abasecode.opencode.base.token.config.TokenConfig;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableCodeToken
public class ShiroConfig {

    @Autowired
    TokenConfig.AuthFilter authFilter;

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        Map<String, Filter> filters = new HashMap<>();
        filters.put("code-auth", new AuthFilter());
        shiroFilter.setFilters(filters);
        Map<String, String> filterMap = new LinkedHashMap<>();
        for (String a : authFilter.getAnnos()) {
            filterMap.put(a, "anon");
        }
        for (String b : authFilter.getAuths()) {
            filterMap.put(b, "code-auth");
        }
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


## Notice
### Shiro need.
Using shiro as a security component requires the introduction of the shiro package.
```xml
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-core</artifactId>
            <version>1.9.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-web</artifactId>
            <version>1.9.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.9.1</version>
        </dependency>
```

### Simple mode example.
``` java
    @Autowired
    TokenHandler tokenHandler;
    
    ...
    
    Token token = tokenHandler.createToken(user);

```
### About TokenUser.java
```java
public class TokenUser implements Serializable {
    // userId
    private Integer userId;
    // userName
    private String userName;
    // tel or email
    private String userTel;
    // login time
    private LocalDateTime userLoginTime;
    // 1 is forbidden, 0 is allowed.
    private Integer status;
    /**
     * If used simple mode, it's no required.
     */
    private Set<String> userPermissionSet;
    /**
     * If used simple mode, it's no required.
     */
    private Set<String> userRolesSet;
}
```