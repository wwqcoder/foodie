package cn.wwq.controller;

import cn.wwq.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("redis")
public class RedisController {

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("set")
    public String set(String key,String value){
        redisOperator.set(key, value);
        return "OK!!";
    }

    @GetMapping("get")
    public String get(String key){
        return redisOperator.get(key);
    }

    @GetMapping("del")
    public String del(String key){
        redisOperator.del(key);
        return "delete OK !!";
    }
}
