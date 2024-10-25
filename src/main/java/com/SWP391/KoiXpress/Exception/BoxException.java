package com.SWP391.KoiXpress.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BoxException extends RuntimeException{
    public BoxException (String message){
        super(message);
    }
}
