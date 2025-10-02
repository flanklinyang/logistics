package com.yang.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LogisticsPackageVO {

    private String packageExplain;

    private Double packageWidth;

    private Double packageHigh;

    private Double packageLength;

    private Double packageHeavy;

    private List<LogisticsProductVO> logisticsProductVOList = new ArrayList<>();
}