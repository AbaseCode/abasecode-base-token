package com.abasecode.opencode.base.token.auth;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author Jon
 * e-mail: ijonso123@gmail.com
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://github.com/abasecode">project github</a>
 * url: <a href="https://abasecode.com">AbaseCode.com</a>
 */
public class AuthToken implements AuthenticationToken {
    private final String token;

    public AuthToken(String token) {
        this.token = token;
    }

    /**
     * get token
     *
     * @return token
     */
    @Override
    public String getPrincipal() {
        return token;
    }

    /**
     * get token
     *
     * @return token
     */
    @Override
    public Object getCredentials() {
        return token;
    }
}
