package com.mindful.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    // React 라우팅을 위한 폴백 컨트롤러
    // /api/** 이외의 모든 요청을 index.html로 전달
    @GetMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}