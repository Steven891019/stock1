package com.steven.financial.stock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.steven.financial.stock.model.Stock;
import com.steven.financial.stock.util.Constants;
import com.steven.financial.stock.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class StockDBService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StockRedisService stockRedisService;

    @Autowired
    private RestTemplate restTemplate;

    public void addDataIntoDB(String symbol, String scale, String ma, String datalen) throws Exception {
        Set<Stock> stockList = this.retrieveStockList("stock_" + symbol);
        String baseURL = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=%s&scale=%s&ma=%s&datalen=%s";
        baseURL = String.format(baseURL, symbol, scale, ma, datalen);
        log.info("The url is : " + baseURL);
        ResponseEntity<String> entity = restTemplate.exchange(baseURL, HttpMethod.GET, null, String.class);
        String json = entity.getBody();
        log.info("result from third party API : " + json);

        List<Stock> tpData = JsonUtils.parseJson2List(json);

        List<Stock> redisCache = stockRedisService.getCache(Constants.STOCK_LIST_PER_DAY);

        if (null != tpData && null != redisCache) {
            if (tpData.containsAll(redisCache) && redisCache.containsAll(tpData)) {
                log.error("no data refresh, don't update DB");
            } else {
                for(Stock stock : tpData) {
                    if (new HashSet<Stock>(this.retrieveStockList("stock_" + symbol)).add(stock)) {
                        //add this item into DB
                        addItemInToDB(stock, symbol);
                    } else {
                        log.error("data exists in DB already!!!" + stock.getDay() + "----->" + stock.getTime());
                    }
                }
                stockRedisService.setCache(tpData);
            }
        } else {
            stockRedisService.setCache(new ArrayList<Stock>(stockList));
        }
    }


    private Set<Stock> retrieveStockList(String tableName) {
        String query_sql = "select * from %s";
        query_sql = String.format(query_sql, tableName);
        return new HashSet<Stock>(jdbcTemplate.query(query_sql, new RowMapper<Stock>() {
            @Override
            public Stock mapRow(ResultSet resultSet, int i) throws SQLException {
                Stock stock = new Stock();
                stock.setClose(resultSet.getDouble("close"));
                stock.setDay(resultSet.getString("date") + " " + resultSet.getString("time"));
                stock.setHigh(resultSet.getDouble("high"));
                stock.setLow(resultSet.getDouble("low"));
                stock.setOpen(resultSet.getDouble("open"));
                stock.setTime(resultSet.getString("time"));
                stock.setVolume(resultSet.getLong("volume"));
                return stock;
            }
        }));
    }

    private void addItemInToDB(Stock stock, String symbol) throws Exception {
        String add_sql = "insert into stock_" + symbol + "(close, date, high, low, open, time, volume) values(?,?,?,?,?,?,?)";
        Object[] args = new Object[]{stock.getClose(), stock.getDay().split(" ")[0], stock.getHigh(), stock.getLow(), stock.getOpen(), stock.getDay().split(" ")[1], stock.getVolume()};
        this.jdbcTemplate.update(add_sql, args);
    }
}
