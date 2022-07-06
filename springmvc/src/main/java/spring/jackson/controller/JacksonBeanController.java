package spring.jackson.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.jackson.bean.RedisBase;
import spring.jackson.bean.RedisCluster;

@Controller
public class JacksonBeanController {

    @RequestMapping("/jackson-redis-base")
    @ResponseBody
    public ResponseEntity<RedisBase> jacksonRedisBase(@RequestBody RedisBase baseBean) {
        System.out.println(baseBean);
        return ResponseEntity.ok(baseBean);
    }

    @RequestMapping("/jackson-redis-cluster")
    @ResponseBody
    public ResponseEntity<RedisCluster> jacksonRedisCluster(@RequestBody RedisCluster redisCluster) {
        System.out.println(redisCluster);
        return ResponseEntity.ok(redisCluster);
    }

}
