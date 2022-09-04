package com.abasecode.opencode.base.token.auth;

import com.abasecode.opencode.base.code.CodeException;
import com.abasecode.opencode.base.token.TokenHandler;
import com.abasecode.opencode.base.token.config.TokenConfig;
import com.abasecode.opencode.base.token.entity.Token;
import com.abasecode.opencode.base.token.entity.TokenInfo;
import com.abasecode.opencode.base.token.entity.TokenUser;
import com.abasecode.opencode.base.token.util.CodeRedisUtils;
import com.abasecode.opencode.base.token.util.TokenKeysUtils;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Jon
 * e-mail: ijonso123@gmail.com
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://github.com/abasecode">project github</a>
 * url: <a href="https://abasecode.com">AbaseCode.com</a>
 */
@Component
public class AuthRealm extends AuthorizingRealm {
    @Autowired
    TokenHandler tokenHandler;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CodeRedisUtils codeRedisUtils;

    @Autowired
    TokenConfig.ApiToken apiToken;


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof AuthToken;
    }

    /**
     * doGetAuthorizationInfo
     *
     * @param principals PrincipalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        TokenUser user = (TokenUser) principals.getPrimaryPrincipal();
        Integer userId = user.getUserId();

        Set<String> permsSet = JSON.parseObject(codeRedisUtils
                .getObject(TokenKeysUtils.getPermissionKey(apiToken.getKey(), userId)).toString(), Set.class);
        Set<String> roleSet = JSON.parseObject(codeRedisUtils
                .getObject(TokenKeysUtils.getRolesKey(apiToken.getKey(), userId)).toString(), Set.class);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        info.setRoles(roleSet);
        if (SecurityUtils.getSubject().getPrincipal() == null) {
            throw new CodeException("Authorization required!", HttpStatus.UNAUTHORIZED.value());
        }
        return info;
    }

    /**
     * doGetAuthenticationInfo
     *
     * @param token AuthenticationToken
     * @return AuthenticationInfo
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        if (StringUtils.isBlank(accessToken)) {
            throw new CodeException("Authorization required!!", HttpStatus.UNAUTHORIZED.value());
        }
        Claims claims = null;
        try {
            claims = tokenHandler.getClaimByToken(accessToken);
            if (claims == null || tokenHandler.isTokenExpired(claims.getExpiration())) {
                throw new CodeException("The authorization is invalid, please login again!", HttpStatus.UNAUTHORIZED.value());
            }
            TokenInfo tokenInfo = tokenHandler.getTokenInfoByClaim(accessToken);

            if (!tokenInfo.getAppKey().equals(apiToken.getKey())) {
                throw new CodeException("The token is invalid, check baseKey!", HttpStatus.UNAUTHORIZED.value());
            }
            Token redisToken = JSON.parseObject(codeRedisUtils.getObject(TokenKeysUtils.getTokenKey(tokenInfo.getAppKey(), tokenInfo.getUserId())).toString(), Token.class);
            if (redisToken != null && redisToken.getToken().equals(accessToken)) {
                TokenUser user = JSON.parseObject(codeRedisUtils.getObject(TokenKeysUtils.getUserKey(tokenInfo.getAppKey(), tokenInfo.getUserId())).toString(), TokenUser.class);
                if (user == null) {
                    throw new CodeException("The account is invalid!", HttpStatus.UNAUTHORIZED.value());
                }
                if (user.getStatus() == 1) {
                    throw new CodeException("The account is invalid!!", HttpStatus.UNAUTHORIZED.value());
                }
                return new SimpleAuthenticationInfo(user, accessToken, getName());
            }
        } catch (Exception e) {
            throw new CodeException("The authorization information is wrong!", HttpStatus.UNAUTHORIZED.value());
        }

        throw new CodeException("The authorization is invalid, please login again!!!", HttpStatus.UNAUTHORIZED.value());
    }
}