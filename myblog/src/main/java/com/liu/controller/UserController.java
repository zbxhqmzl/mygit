package com.liu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {
    @RequestMapping("/addElement")
    public String add(){

        return "table";
    }
    @RequestMapping("/deleteElement")
    public String delete(){

        return "table";
    }
    @RequestMapping("/selectElement")
    public String select(){

        return "table";
    }
    @RequestMapping("/createElement")
    public String create(){

        return "table";
    }
}
