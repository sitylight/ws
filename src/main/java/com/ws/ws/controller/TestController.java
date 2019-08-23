package com.ws.ws.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lcb 2019/8/23
 */
@Controller
public class TestController {

    @RequestMapping("/test")
    public String test() {
        return "index";
    }
}
