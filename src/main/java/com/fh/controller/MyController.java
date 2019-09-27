package com.fh.controller;

import com.fh.domain.Person;
import com.fh.domain.ResultVo;
import com.fh.domain.Title;
import com.fh.utils.ResultVoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class MyController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 登录
     * @param person
     * @param request
     * @return
     */
    @RequestMapping("/userLogin")
    public String login(Person person, HttpServletRequest request){
        String sql = "SELECT password FROM ISC_PERSON WHERE PHONE = ?";
        List<Map<String, Object>> resultData = jdbcTemplate.queryForList(sql, person.getPhone());
        String dbPassword = null;

        // 判断数据库中是否有值
        if(!CollectionUtils.isEmpty(resultData)){
            dbPassword = (String) resultData.get(0).get("password");
            // 判断密码是否正确
            if(!StringUtils.isEmpty(dbPassword)&&dbPassword.equals(person.getPassword())){
                request.getSession().setAttribute("person",person);
                return "redirect:index.jsp";
            }else {
                return "jsp/login";
            }
        }else{
            return "jsp/login";
        }
    }

    /**
     * 保存内容
     * @param content
     * @return
     */
    @RequestMapping("/saveContent")
    @ResponseBody
    public ResultVo saveContent(String content){
        String sql = "INSERT INTO ISC_CONTENT VALUES(?,?)";
        jdbcTemplate.update(sql,content,new Date());
        return ResultVoUtil.success("OK");
    }

    /**
     * 搜索内容
     */
    @RequestMapping("/searchContent")
    @ResponseBody
    public ResultVo searchContent(String content){
        String sql = "SELECT CONTENT,TO_CHAR(CREATETIME,'YYYY-MM-DD HH24:mm:ss') CREATETIME FROM ISC_CONTENT WHERE  " +
                "CONTENT LIKE '%'||REPLACE(REPLACE(?,'%' ,'\\%' ),'_','\\_')||'%' ESCAPE '\\'";
        List<Map<String, Object>> resData = jdbcTemplate.queryForList(sql, content);

        if(!CollectionUtils.isEmpty(resData)) {
            System.out.println(resData.get(0).get("CREATETIME"));
            return ResultVoUtil.success(resData);
        }else {
            return ResultVoUtil.error(404, "暂无内容!");
        }
    }

    /**
     * 保存json格式的信息
     */
    @RequestMapping(value = "/saveJsonContent", method = RequestMethod.POST)
    @ResponseBody
    public ResultVo saveJsonContent(@RequestBody Title title){
        String sql = "INSERT INTO ISC_CONTENT VALUES(?,?)";
        jdbcTemplate.update(sql,title.getContent(),new Date());
        return ResultVoUtil.success("OK");
    }
}
