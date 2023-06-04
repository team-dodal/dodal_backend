package com.dodal.meet.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/")
    public String sample(){
        String profile = System.getProperty("spring.profiles.active");
        return "현재 서버는 " + profile +" 모드입니다.";
    }
}
