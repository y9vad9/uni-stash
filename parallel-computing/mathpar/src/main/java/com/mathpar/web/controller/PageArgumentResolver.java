package com.mathpar.web.controller;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import com.mathpar.number.Ring;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class PageArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger LOG = getLogger(PageArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return
                methodParameter.getParameterAnnotation(PageParam.class) != null
                        && methodParameter.getParameterType().equals(Page.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        if (this.supportsParameter(methodParameter)) {
            HttpSession session = webRequest.getNativeRequest(HttpServletRequest.class).getSession();
            Page page = null;
            if (session != null) {
                page = (Page) session.getAttribute("page");
                if (page == null) {
                    page = new Page(Ring.ringR64xyzt,true);
                    page.setSessionId(session.getId());
                    session.setAttribute("page", page);
                }
            }
            return page;
        } else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }
}
