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
``` xml
<dependency>
    <groupId>com.abasecode.opencode</groupId>
    <artifactId>abasecode-base-token</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Step 2: 配置 application.yaml
``` yaml
app:
  token:
    key: String format. the key in redis.
    secret: String format. the token secret. example: "3d15d32654bc1af61759a3bacbc0c78a"
    expire: Integer format. the token expire time (seconds).   
```

## Step 3: 添加 ShiroConfig.java

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

## 注意点 1: 需要redis
需要引入redis
``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
## 注意点 2: app.token.key 和简单模式。
app.token.key的默认值是 "adm-token"。
如果你不使用 shiro 的授权和验证功能，你可以使用简单模式。 简单模式允许你在一个项目中使用多个自定义密钥。

### 示例.
``` java
    @Autowired
    TokenHandler tokenHandler;
    
    ...
    
    Token token = tokenHandler.createToken(user);

```