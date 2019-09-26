package com.fh.filter;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssAndSqlHttpServletRequestWrapper extends HttpServletRequestWrapper {

    HttpServletRequest orgRequest = null;

    public XssAndSqlHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        orgRequest = request;
    }

    /**
     * xss过滤
     * @param param
     * @return
     */
    private static String xssEncode(String param){
        if(StringUtils.isEmpty(param)){
            return param;
        }
        param = param.replaceAll("<", "＜").replaceAll(">","＞");
        return param;
    }

    public static HttpServletRequest getOrRequest(HttpServletRequest req){
        if(req instanceof XssAndSqlHttpServletRequestWrapper){
            return ((XssAndSqlHttpServletRequestWrapper)req).getOrgRequest();
        }
        return req;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(xssEncode(name));
        if(value != null){
            value = xssEncode(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if(values == null){
            String[] tarr = {};
            return tarr;
        }
        int count = values.length;
        String[] encodeValues = new String[count];
        for (int i = 0; i < count; i++){
            encodeValues[i] = xssEncode(values[i]);
        }
        return encodeValues;
    }

    @Override
    public String getParameter(String name){
        String value = super.getParameter(xssEncode(name));
        if(!StringUtils.isEmpty(value)){
            value = xssEncode(value);
        }
        return value;
    }

    public HttpServletRequest getOrgRequest(){
        return orgRequest;
    }
}
