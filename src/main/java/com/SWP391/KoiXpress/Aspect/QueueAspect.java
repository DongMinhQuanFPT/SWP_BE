package com.SWP391.KoiXpress.Aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Aspect
public class QueueAspect {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void processQueue(){
        while(true){
            try{
                Runnable task = queue.take();
                task.run();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
