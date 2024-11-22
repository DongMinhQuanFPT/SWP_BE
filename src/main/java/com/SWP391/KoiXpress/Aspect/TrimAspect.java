package com.SWP391.KoiXpress.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Aspect
@Component
public class TrimAspect {

    @Before("@annotation(Trimmed)")
    public void trimStrings(JoinPoint joinPoint){
        for(Object arg : joinPoint.getArgs()){
            if(arg != null){
                trimAllStringFields(arg);
            }
        }
    }

    private void trimAllStringFields(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Đảm bảo có quyền truy cập vào các trường private
            try {
                // Kiểm tra nếu trường là kiểu String
                if (field.getType() == String.class) {
                    String value = (String) field.get(obj);
                    if (value != null) {
                        field.set(obj, value.trim()); // Trim chuỗi
                    }
                }
                // Kiểm tra nếu trường là kiểu mảng String[]
                else if (field.getType() == String[].class) {
                    String[] array = (String[]) field.get(obj);
                    if (array != null) {
                        for (int i = 0; i < array.length; i++) {
                            if (array[i] != null) {
                                array[i] = array[i].trim();
                            }
                        }
                        field.set(obj, array);
                    }
                }
                // Kiểm tra nếu trường là kiểu List<String>
                else if (field.getType() == List.class) {
                    List<String> list = (List<String>) field.get(obj);
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            String item = list.get(i);
                            if (item != null) {
                                list.set(i, item.trim());
                            }
                        }
                        field.set(obj, list);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
