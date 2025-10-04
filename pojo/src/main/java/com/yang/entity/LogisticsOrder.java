package com.yang.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class LogisticsOrder {

    private Long id;

    private String userSign;

    private String orderNumber;

    private String orderingProducts;

    private String sendMobilePhone = "";

    private String sendCall = "";

    private String senderName;

    private String senderCompany = "";

    private String senderAddress;

    private String senderAddressOne = "";

    private String senderProvince;

    private String senderCity;

    private String senderPostalCode;

    private String senderStreet = "";

    private String fromPlace;

    private String destination;

    private String recipientName;

    private String recipientCompany = "";

    private String recipientAddress;

    private String recipientAddressOne = "";

    private String recipientProvince;

    private String recipientCity;

    private String recipientPostalCode;

    private String recipientStreet = "";

    private String recipientPhone = "";

    private String recipientMobilePhone = "";

    private String transferNo;

    private BigDecimal freight;

    private String orderStatus;

    private String passengerOrder;

    private String quantityWeight;

    private BigDecimal weight;

    private String merchantNumber;

    private String pdfUrl;

    private Date createTime;

    private Date updateTime;

    private List<LogisticsPackage> packageList;

}