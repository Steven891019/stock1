package com.steven.financial.stock.controller;

import com.steven.financial.stock.service.StockDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private StockDBService stockDBService;


    @GetMapping("/index")
    @ResponseBody
    public String index() {
        return "hello";
    }

    @GetMapping("/add")
    public void add(HttpServletRequest request) {
        String symbol = request.getParameter("symbol");
        String scale = request.getParameter("scale");
        String ma = request.getParameter("ma");
        String datalen = request.getParameter("datalen");
        try {
            stockDBService.addDataIntoDB(symbol, scale, ma, datalen);
        } catch (Exception e) {
            log.error("error occurring when adding item to DB", e);
        }
    }
}
