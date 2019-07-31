package com.example.endpoint;

import com.example.beans.ComplexBeanAware;
import com.example.beans.MyPrototypeScopeBean;
import com.example.beans.MySingletonScopeBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SayHelloController implements ComplexBeanAware {

  private String simpleBean;

  private String complexBean;
  private MySingletonScopeBean singletonScopeBean;
  private MyPrototypeScopeBean prototypeScopeBean;

  @Autowired
  public SayHelloController(@Qualifier("simple") final String bean) {
    this.simpleBean = bean;
  }

  @RequestMapping(value = "say/{msg}", method = RequestMethod.GET)
  public String sayHello(@PathVariable("msg") final String msg) {
    log.debug("starting say message endpoint request");
    String result = String.format(
        "The message [%s] " + "simple bean value [%s] " + "complex bean value [%s] " + "singleton [%s] "
            + "prototype [%s] ",
        msg, simpleBean, complexBean, singletonScopeBean.getMsg(), prototypeScopeBean.getMsg());
    log.info("returning from controller {}", result);
    return result;
  }

  @Qualifier("complex")
  @Autowired
  @Override
  public void setComplexBean(String bean) {
    this.complexBean = bean;
  }

  @Autowired
  public void setMySingletonScopeBean(MySingletonScopeBean singletonScopeBean) {
    this.singletonScopeBean = singletonScopeBean;
  }

  @Autowired
  public void setMyPrototypeScopeBean(MyPrototypeScopeBean prototypeScopeBean) {
    this.prototypeScopeBean = prototypeScopeBean;
  }
}
