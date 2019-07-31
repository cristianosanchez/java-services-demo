package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope("prototype")
@Component
public class MyPrototypeScopeBean {

    private final String msg;

    public MyPrototypeScopeBean() {
        log.info("creating MyPrototypeScopeBean");
        this.msg = String.format("MyPrototypeScopeBean timestamp %d", System.currentTimeMillis());
    }

    public String getMsg() {
        return msg;
    }
}
