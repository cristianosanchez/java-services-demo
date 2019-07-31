package com.example.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope("singleton") // the default, we can leave this.
@Component
public class MySingletonScopeBean {

    private final String msg;

    public MySingletonScopeBean() {
        log.info("creating MySingletonScopeBean");
        this.msg = String.format("MySingletonScopeBean timestamp %d", System.currentTimeMillis());
    }

    public String getMsg() {
        return msg;
    }
}
