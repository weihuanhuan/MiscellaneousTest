package spring.jdbc.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.jdbc.transaction.service.MybatisTransactionService;

@Controller
@RequestMapping("/mybatisTransaction")
public class MybatisTransactionController {

    @Autowired
    private MybatisTransactionService mybatisTransactionService;

    @RequestMapping("/insert-fail-with-transactional")
    @ResponseBody
    public void insertFailWithTransactional() {
        mybatisTransactionService.mybatisInsertFailWithTransactional();
    }

    @RequestMapping("/insert-fail-without-transactional")
    @ResponseBody
    public void insertFailWithoutTransactional() {
        mybatisTransactionService.mybatisInsertFailWithoutTransactional();
    }

}
