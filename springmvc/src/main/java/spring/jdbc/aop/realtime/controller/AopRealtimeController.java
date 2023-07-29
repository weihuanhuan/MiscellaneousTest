package spring.jdbc.aop.realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.jdbc.aop.realtime.service.AopRealtimeService;

@Controller
@RequestMapping("/mybatisAopRealtime")
public class AopRealtimeController {

    @Autowired
    private AopRealtimeService aopRealtimeService;

    @RequestMapping("/update-success-with-transactional")
    @ResponseBody
    public String updateSuccessWithTransactional() {
        String result = aopRealtimeService.updateSuccessWithTransactional();
        return result;
    }

}
