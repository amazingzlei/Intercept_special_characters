package com.fh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping(value = "index")
    public String gotoIndex(){
        return "redirect:index.jsp";
    }

    @RequestMapping(value = {"login","login.jsp"})
    public String gotoLogin(){
        return "jsp/login";
    }
}
