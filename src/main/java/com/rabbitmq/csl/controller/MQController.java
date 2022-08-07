package com.rabbitmq.csl.controller;

import com.rabbitmq.csl.service.MQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j
public class MQController {

    @Autowired
    private MQService mqService;

    @PostMapping("send")
    public String send(@RequestParam String msg){
        return mqService.sendMsg(msg);
    }

    @PostMapping("sendfanout")
    public String sendfanout(@RequestParam String msg){
        return mqService.sendfanoutMsg(msg);
    }

    @RequestMapping("sendtopic")
    public String sendtopic(@RequestParam String msg,
                            @RequestParam String routingKey){
        return mqService.sendtopic(msg,routingKey);
    }

        @RequestMapping("delayMsg")
    public String delayMsg2(String msg, Integer delayTime) {
        log.info("当前时间：{},收到请求，msg:{},delayTime:{}", new Date(), msg, delayTime);
        return mqService.sendDelayMsg(msg, delayTime);
    }


}
