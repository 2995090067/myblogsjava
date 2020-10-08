package com.zy.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 接收前端传来的用户名和密码
 */
@Data
public class LoginDto implements Serializable {

    @NotBlank(message = "昵称不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
