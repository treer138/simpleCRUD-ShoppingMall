package com.example.study.controller;

import com.example.study.model.SearchParam;
import com.example.study.model.network.Header;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") //Localhost:8080/api
public class GetController {
    @RequestMapping(method = RequestMethod.GET, path = "/getMethod")
    public String getRequest(){

        return "Hi getMethod";

    }
    @GetMapping("/getParameter") //Localhost:8080/api/getParameter?id=1234&password=abcd
    public String getParameter(@RequestParam String id, @RequestParam(name = "password") String pwd) {
        String password = "Bbbb";

        System.out.println(id);
        System.out.println(pwd);

        return id+pwd;
    }
    @GetMapping("/getMultiParameter")
    public SearchParam getMultiparameter(SearchParam searchParam) {

        System.out.println(searchParam.getAccount());
        System.out.println(searchParam.getEmail());
        System.out.println(searchParam.getPage());

        //{"account" : "", "email" : "", "page" : 0}
        return searchParam;

    }

    @GetMapping("/header")
    public Header getHeader() {

        //{"resultCode: "ok", description: "ok""}
        return Header
                .builder()
                .resultCode("OK")
                .description("OK")
                .build();
    }
}
