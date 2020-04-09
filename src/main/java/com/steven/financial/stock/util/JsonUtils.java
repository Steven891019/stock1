package com.steven.financial.stock.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.steven.financial.stock.model.Stock;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;

public class JsonUtils {

    public static List<Stock> parseJson2List(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(ALLOW_UNQUOTED_FIELD_NAMES, true);
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Stock.class);
        List<Stock> stockList = mapper.readValue(json, collectionType);
        return stockList;
    }
}
