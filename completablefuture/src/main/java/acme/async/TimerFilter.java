package acme.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class TimerFilter implements Filter {

    static Logger logger = LoggerFactory.getLogger("acme.async");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        long t = System.currentTimeMillis();
        chain.doFilter(request, response);
        long dt = System.currentTimeMillis() - t;
        logger.info("transaction took #{} millis", dt);
    }
}
