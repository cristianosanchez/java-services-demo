package com.example.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public interface ComplexBeanAware {

    // To avoid multiple-inheritance, annotations are not inherited.
    //@Qualifier("complex")
    //@Autowired
    void setComplexBean(String bean);
}
