package com.tanhua.sso.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @program: my-tanhua
 * @description: 发生错误返回的对象，未发生错误返回200即可
 * @author: HongXin
 * @create: 2021-08-26 20:47
 */

@Data
@Builder
public class ErrorResult {
    private String errCode;
    private String errMessage;
}
