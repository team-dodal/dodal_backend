package com.dodal.meet.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/")
    @ApiOperation(value = "서버 profile 확인", notes = "EC2 서버 프로필 정보 확인 (dev, prod)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "정상"),
            @ApiResponse(code = 500, message = "서버오류")
    })
    public String sample(){
        String profile = System.getProperty("spring.profiles.active");
        return "현재 서버는 " + profile +" 모드입니다.";
    }
}
