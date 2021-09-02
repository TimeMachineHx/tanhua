package com.tanhua.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: my-tanhua
 * @description: 查询参数对象
 * @author: HongXin
 * @create: 2021-09-02 10:37
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendUserQueryParam implements Serializable {
    private Integer page = 1; //当前页数
    private Integer pagesize = 10; //页尺寸
    private String gender; //性别 man woman
    private String lastLogin; //近期登陆时间
    private Integer age; //年龄
    private String city; //居住地
    private String education; //学历
}
