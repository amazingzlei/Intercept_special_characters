package com.fh.inteceptor;

import com.fh.domain.Person;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        Person person = (Person)request.getSession().getAttribute("person");
        if(person == null){
            String type =
                    request.getHeader("X-Requested-With")==null?"":request.getHeader("X-Requested-With");
            // 判断是否为ajax请求
            if("XMLHttpRequest".equals(type)){
                response.setHeader("sessionStatus", "timeout");
                response.setHeader("clflag", "http://10.1.21.221:8090/isc/login");
            }else{
                request.getRequestDispatcher("jsp/login.jsp").forward(request,response);
            }
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
