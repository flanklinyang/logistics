package com.yang.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LogisticsProduct {

    private Long id;

    private Long packageId;

    private String chineseName;

    private String englishName;

    private String specie;

    private Integer number;

    private BigDecimal money;

    private BigDecimal price;

    private String currency;

    private String specifications;

}
