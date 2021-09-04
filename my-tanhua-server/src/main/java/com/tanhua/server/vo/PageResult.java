package com.tanhua.server.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @program: my-tanhua
 * @description: 列表查询返回对象
 * @author: HongXin
 * @create: 2021-09-02 10:43
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {

    private Integer counts = 100;//总记录数
    private Integer pagesize = 0;//页大小
    private Integer pages = 10;//总页数
    private Integer page = 0;//当前页码
    private List<?> items = Collections.emptyList(); //列表

}