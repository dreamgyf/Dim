package com.dreamgyf.dim.utils;

import java.lang.reflect.Field;
import java.util.Map;

public class BeanUtils {

    public static <T> T map2Bean(Map<String, Object> map, Class<T> clz) throws Exception {
        if(map == null)
            return null;
        T res = null;
        res = clz.newInstance();
        Field[] fields = clz.getDeclaredFields();
        for(Field field : fields) {
            field.set(res,map.get(field.getName()));
        }
        return res;

    }
}
