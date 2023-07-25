package spring.jdbc.aop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.jdbc.aop.service.AopTransactionService;

@Controller
@RequestMapping("/mybatisAopTransaction")
public class AopTransactionController {

    @Autowired
    private AopTransactionService aopTransactionService;

    @RequestMapping("/update-success-with-transactional")
    @ResponseBody
    public String updateSuccessWithTransactional() {
        String result = aopTransactionService.updateSuccessWithTransactional();
        return result;
    }

}
