package com.steven.financial.stock.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Stock {
    private String day;
    private String time;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return day.equals(stock.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }
}
