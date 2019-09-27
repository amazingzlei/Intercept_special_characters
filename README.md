# Intercept_special_characters
解决xss攻击、sql注入、未登录状态发送ajax请求时转发登录页面
1.未登录状态发送ajax请求时转发登录页面
（1）首先在拦截器中判断是否为ajax请求，如果是则向请求头中添加时延信息
String type =
                    request.getHeader("X-Requested-With")==null?"":request.getHeader("X-Requested-With");
            // 判断是否为ajax请求
            if("XMLHttpRequest".equals(type)){
                response.setHeader("sessionStatus", "timeout");
                response.setHeader("clflag", "http://10.1.21.221:8090/isc/login");
            }else{
                request.getRequestDispatcher("jsp/login.jsp").forward(request,response);
            }
（2）在前端页面设置$.ajaxSetup
// 判断是否时延，如果时延则表示为登录，重定向至登录页面
    $.ajaxSetup({
        complete:function (xhr,status) {
            var sessionStatus = xhr.getResponseHeader('sessionStatus');
            if(sessionStatus == 'timeout'){
                var top = getTopWindow();
                var loginAddress = xhr.getResponseHeader('clflag');
                top.location.href = loginAddress;
            }
        }
    });
2.解决xss攻击
2.1 非json格式的入参
（1）配置一个过滤器
public class XssAndSqlFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        XssAndSqlHttpServletRequestWrapper xssRequest =
                new XssAndSqlHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(xssRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
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
（2）在web.xml中注册该过滤器
<filter>
        <filter-name>XssAndSqlFilter</filter-name>
        <filter-class>com.fh.filter.XssAndSqlFilter</filter-class>
    </filter>
    <!--过滤特殊字符-->
    <filter-mapping>
        <filter-name>XssAndSqlFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
2.2 JSON格式的入参
(1)编写一个类，并继承ObjectMapper，然后编写业务逻辑
public class ToolKitsObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 4359709211352400087L;

    public ToolKitsObjectMapper(){
        SimpleModule module = new SimpleModule("XssStringJsonSerializer");
        module.addDeserializer(String.class, new JsonHtmlXssDeserializer(String.class));
        this.registerModule(module);
    };

    /*
        对出参进行转义
     */
    class JsonHtmlXssSerializer extends JsonSerializer<String>{

        public JsonHtmlXssSerializer(Class<String> string){
            super();
        }

        @Override
        public Class<String> handledType(){
            return String.class;
        }

        @Override
        public void serialize(String value,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            if(!StringUtils.isEmpty(value)){
                String encodedValue = HtmlUtils.htmlEscape(value);
                jsonGenerator.writeString(encodedValue);
            }
        }
    }

    /**
     * 对入参进行转义
     */
    class JsonHtmlXssDeserializer extends JsonDeserializer<String>{
        public JsonHtmlXssDeserializer(Class<String> string){
            super();
        }

        @Override
        public String deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String value = jsonParser.getValueAsString();
            if(!StringUtils.isEmpty(value)){
                return value.replaceAll("<", "＜").replaceAll(">","＞");
            }
            return value;
        }

        @Override
        public Class<String> handledType(){
            return String.class;
        }
    }
}
（2）在springmvc配置文件中注册该bean
 <mvc:annotation-driven>
        <mvc:message-converters register-defaults="true">
            <bean id="stringHttpMessageConverter" class="org.springframework.http.converter.StringHttpMessageConverter">
                <property name="supportedMediaTypes"  value="application/json;charset=UTF-8"/>
            </bean>
            <bean id="mappingJackson2HttpMessageConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
                <property name="supportedMediaTypes">
                    <list>
                        <value>text/hmtl;charset=UTF-8</value>
                        <value>application/json;charset=UTF-8</value>
                    </list>
                </property>
                <property name="objectMapper">
                    <bean class="com.fh.filter.ToolKitsObjectMapper"></bean>
                </property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>
3. oracle注入问题
    将%和_替换成\% \_然后进行转义

