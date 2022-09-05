# 关于AbaseCode
AbaseCode OpenCode是一套开源合集。包括基础包、工具包、安全包、token包、支付包、excel包等。

开源项目的组件做到开箱即用，方便更多的开发者节省重复的工作，更专注于业务逻辑代码编写。

我是Jon，一名全栈开发者，专注于学习和传播技术知识。希望这些工具包能够帮上你，欢迎有的朋友加入这个开源项目。

project homepage : https://abasecode.com

project github : https://github.com/abasecode

Jon's blog : https://jon.wiki

e-mail: ijonso123@gmail.com

# 关于 abasecode-base-token
一个token库。


# 开始使用
## Step 1: 配置 pom.xml
配置如下，注意，需要引入redis
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

## Step 2: 配置 application.yaml
配置如下：
app:auth-filter:annos 无需授权的路径，一行一个。
app:auth-filter:auths 需要授权认证的路径，一行一个。
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

## Step 3: 添加 ShiroConfig.java
以下代码可以直接复制过去使用。
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
## Step 4: 添加注解
```java
@EnableCodeToken
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
```

## Step 5: 完成

## 注意
### 需要引入Shiro
使用shiro作为安全组件，需要引入shiro包。
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

### 简单示例
``` java
    @Autowired
    TokenHandler tokenHandler;
    
    ...
    
    Token token = tokenHandler.createToken(user);

```
### 关于 TokenUser.java
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
    // 1 表示用户被禁止登录, 0 表示用户允许登录
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