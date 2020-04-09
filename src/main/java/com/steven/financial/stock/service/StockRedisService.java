package com.steven.financial.stock.service;

import com.steven.financial.stock.model.Stock;
import com.steven.financial.stock.util.Constants;
import com.steven.financial.stock.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StockRedisService {

    @Autowired
    private RedisUtil redisUtil;

    public void setCache(List<Stock> stockList) {
        redisUtil.set(Constants.STOCK_LIST_PER_DAY, stockList, 60*60*24);
    }

    public List<Stock> getCache(String key) {
        return (List<Stock>) redisUtil.get(Constants.STOCK_LIST_PER_DAY);
    }
}
