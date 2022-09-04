package com.abasecode.opencode.base.token.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Jon
 * e-mail: ijonso123@gmail.com
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://github.com/abasecode">project github</a>
 * url: <a href="https://abasecode.com">AbaseCode.com</a>
 */
@Data
@Accessors(chain = true)
public class TokenUser implements Serializable {
    private static final long serialVersionUID = -1250173609480244702L;
    private Integer userId;
    private String userName;
    private String userTel;
    private LocalDateTime userLoginTime;
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
