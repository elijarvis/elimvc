package com.eli.product.elimvc.business.controller;

import com.eli.product.elimvc.annotation.Controller;
import com.eli.product.elimvc.annotation.Qualifier;
import com.eli.product.elimvc.annotation.RequestMapping;
import com.eli.product.elimvc.annotation.RequestParam;
import com.eli.product.elimvc.business.service.EliService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author eli
 * @description 控制层
 */
@Controller
@RequestMapping("/eli")
public class EliController {
    @Qualifier("eliService")
    EliService eliService;
    @RequestMapping("/test")
    public void test(HttpServletRequest req, HttpServletResponse resp, @RequestParam("reqName") String name){
        try {
            PrintWriter printWriter = resp.getWriter();
            printWriter.write(eliService.test(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
