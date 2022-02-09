package com.example.zuulservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class ZuulLoggingFilter extends ZuulFilter {

    @Override
    public Object run() throws ZuulException {  // 실행
        log.info("*********** printng logs: ");

        RequestContext ctx =RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("***********" + request.getRequestURI());

        return null;
    }

    @Override
    public String filterType() {    // 필터타입 사전 : pre , 경로 : route , 사후 : post , 에러 : error
        return "pre";
    }

    @Override
    public int filterOrder() {  // 필터순서
        return 1;
    }

    @Override
    public boolean shouldFilter() { // 필터사용여부
        return true;
    }
}
