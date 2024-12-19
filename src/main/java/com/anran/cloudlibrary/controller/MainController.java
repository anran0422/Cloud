package com.anran.cloudlibrary.controller;

import com.anran.cloudlibrary.common.BaseResponse;
import com.anran.cloudlibrary.common.ResultUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = {"http/localhost:5173"}, allowCredentials = "true")
public class MainController {

    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("检查成功！");
    }
}
