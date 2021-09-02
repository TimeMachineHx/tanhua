package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: my-tanhua
 * @description: 今日佳人查询返回的数据类型
 * @author: HongXin
 * @create: 2021-08-30 20:44
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayBest {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender; //性别 man woman
    private Integer age;
    private String[] tags;
    private Double fateValue; //缘分值
}
