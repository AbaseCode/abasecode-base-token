package com.abasecode.opencode.base.token.auth;

import com.abasecode.opencode.base.code.CodeException;
import com.abasecode.opencode.base.code.CodeResult;
import com.abasecode.opencode.base.util.CodeHttpUtils;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jon
 * e-mail: ijonso123@gmail.com
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://github.com/abasecode">project github</a>
 * url: <a href="https://abasecode.com">AbaseCode.com</a>
 */
public class AuthFilter extends AuthenticatingFilter {

    /**
     * createToken
     *
     * @param request  ServletRequest
     * @param response ServletResponse
     * @return AuthenticationToken
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return new AuthToken(token);
    }

    /**
     * isAccessAllowed
     *
     * @param request     ServletRequest
     * @param response    ServletResponse
     * @param mappedValue Object
     * @return boolean
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return ((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name());
    }

    /**
     * onAccessDenied
     *
     * @param request  ServletRequest
     * @param response ServletResponse
     * @return boolean
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Origin", CodeHttpUtils.getOrigin());
            httpResponse.setContentType("application/json;charset=UTF-8");
            String json = JSONObject.toJSONString(CodeResult.error(HttpStatus.SC_UNAUTHORIZED, "Need authorization!"));
            httpResponse.getWriter().print(json);
            return false;
        }

        return executeLogin(request, response);
    }

    /**
     * executeLogin
     *
     * @param request  ServletRequest
     * @param response ServletResponse
     * @return boolean
     * @throws Exception
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = this.createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        } else {
            try {
                Subject subject = this.getSubject(request, response);
                subject.login(token);
                return this.onLoginSuccess(token, subject, request, response);
            } catch (AuthenticationException ex) {
                return this.onLoginFailure(token, ex, request, response);
            }
        }
    }


    /**
     * onLoginFailure
     *
     * @param token    AuthenticationToken
     * @param e        AuthenticationException
     * @param request  ServletRequest
     * @param response ServletResponse
     * @return boolean
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", CodeHttpUtils.getOrigin());
        try {
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            CodeResult result = CodeResult.error(HttpStatus.SC_UNAUTHORIZED, throwable.getMessage());
            String json = JSONObject.toJSONString(result);
            httpResponse.getWriter().print(json);
        } catch (IOException ex) {

        }

        return false;
    }

    /**
     * getRequestToken
     *
     * @param httpRequest HttpServletRequest
     * @return String
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        String token = "";
        try {
            token = httpRequest.getHeader("token");
            if (StringUtils.isEmpty(token)) {
                token = httpRequest.getParameter("token");
            }
            return token;
        } catch (Exception e) {
            throw new CodeException("Token is null!");
        }
    }


}