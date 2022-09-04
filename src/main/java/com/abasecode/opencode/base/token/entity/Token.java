package com.abasecode.opencode.base.token.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jon
 * e-mail: ijonso123@gmail.com
 * url: <a href="https://jon.wiki">Jon's blog</a>
 * url: <a href="https://github.com/abasecode">project github</a>
 * url: <a href="https://abasecode.com">AbaseCode.com</a>
 */
@Data
public class Token implements Serializable {
    private static final long serialVersionUID = -1801118915306343230L;
    private String token;
    private Long expire;
}
