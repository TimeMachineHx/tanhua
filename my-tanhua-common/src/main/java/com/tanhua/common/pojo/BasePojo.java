package com.tanhua.common.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @program: my-tanhua
 * @description: 创建时间、更新时间自动填充
 * @author: HongXin
 * @create: 2021-08-26 19:11
 */

@Data
public class BasePojo {
    @TableField(fill = FieldFill.INSERT)
    private Date created;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;
}
