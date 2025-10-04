package com.yang.entity;

import lombok.Data;
import java.util.List;

@Data
public class LogisticsPackage {

    private Long id;

    private Long orderId;

    private String packageExplain = "";

    private String packageWidth;

    private String packageHigh;

    private String packageLength;

    private String packageHeavy;

    private List<LogisticsProduct> specSubList;

}
