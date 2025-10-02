package com.yang.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LogisticsOrderVO {

    private String transferNo;

    private Double freight;

    private String pdfUrl;

    private String orderStatus;

    private String passengerOrder;

    private String quantityWeight;

    private Double weight;

    private String merchantNumber;

    private String userSign;

    private String orderNumber;

    private String orderingProducts;

    private String sendMobilePhone;

    private String sendCall;

    private String senderName;

    private String senderCompany;

    private String senderAddress;

    private String senderAddressOne;

    private String senderProvince;

    private String senderCity;

    private String senderPostalCode;

    private String senderStreet;

    private String fromPlace;

    private String recipientName;

    private String recipientCompany;

    private String recipientAddress;

    private String recipientAddressOne;

    private String recipientProvince;

    private String recipientCity;

    private String recipientPostalCode;

    private String recipientStreet;

    private String recipientPhone;

    private String recipientMobilePhone;

    private String destination;

    private List<LogisticsPackageVO> packageList = new ArrayList<>();


}