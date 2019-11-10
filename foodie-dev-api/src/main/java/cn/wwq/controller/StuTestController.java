package cn.wwq.controller;

import cn.wwq.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class StuTestController {

    @Autowired
    private StuService stuService;

    @GetMapping("/stu/{id}")
    public Object getInfo(@PathVariable int id){
        return stuService.getStuInfo(id);
    }

    @PostMapping("/stu")
    public String saveStu(){

        try {
            stuService.saveStu();
            return "添加成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "添加失败";
        }
    }

    @PutMapping("/stu/{id}")
    public String updateStu(@PathVariable int id){

        try {
            stuService.updateStu(id);
            return "更新成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "更新失败";
        }
    }

    @DeleteMapping("/stu/{id}")
    public String deleteStu(@PathVariable int id){
        try {
            stuService.deleteStu(id);
            return "删除成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}
