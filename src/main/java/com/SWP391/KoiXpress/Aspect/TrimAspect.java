package com.SWP391.KoiXpress.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

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
        for(Field field : obj.getClass().getDeclaredFields()){
            if(field.getType() == String.class){
                field.setAccessible(true);
                try{
                    String value = (String) field.get(obj);
                    if(value != null){
                        field.set(obj,value.trim());
                    }
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
