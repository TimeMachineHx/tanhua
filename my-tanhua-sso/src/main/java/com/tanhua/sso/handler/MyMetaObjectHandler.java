package com.tanhua.sso.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @program: my-tanhua
 * @description: 对填充字段的处理（更新时间、创建时间）
 * @author: HongXin
 * @create: 2021-08-26 19:22
 */

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Object created = getFieldValByName("created", metaObject);
        if(null == created){
            setFieldValByName("created",new Date(),metaObject);
        }
        Object updated = getFieldValByName("updated", metaObject);
        if(null == updated){
            setFieldValByName("updated",new Date(),metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
         setFieldValByName("updated",new Date(),metaObject);
    }
}
