package spring.jdbc.mybatis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.jdbc.mybatis.bean.MybatisRedis;
import spring.jdbc.mybatis.service.RedisService;

import java.util.List;

@Controller
@RequestMapping("/mybatisRedis")
public class MybatisController {

    @Autowired
    private RedisService<MybatisRedis> redisService;

    @RequestMapping("/list")
    @ResponseBody
    public String mybatisRedisList() {
        List<MybatisRedis> listRedis = redisService.listRedis();
        return String.valueOf(listRedis.size());
    }

    @RequestMapping("/insert")
    @ResponseBody
    public String mybatisRedisInsert() {
        MybatisRedis mybatisRedis = redisService.insertRedis();
        return mybatisRedis.getName();
    }

    @RequestMapping("/update")
    @ResponseBody
    public String mybatisRedisUpdate() {
        MybatisRedis mybatisRedis = redisService.updateRedisByName();
        return mybatisRedis.getConfigText();
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String mybatisRedisDelete() {
        MybatisRedis mybatisRedis = redisService.deleteRedisByName();
        return mybatisRedis.getName();
    }

}
