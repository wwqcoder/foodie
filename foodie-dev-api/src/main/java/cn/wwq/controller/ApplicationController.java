package cn.wwq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class ApplicationController {

    final static Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    @GetMapping("/hello")
    public String hello(){

        LOGGER.info("info");
        LOGGER.debug("debug");
        LOGGER.error("error");
        LOGGER.warn("warn");
        return "Hello World";
    }
}
