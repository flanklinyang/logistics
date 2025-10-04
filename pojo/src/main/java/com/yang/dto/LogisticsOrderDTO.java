package com.yang.dto;

import lombok.Data;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LogisticsOrderDTO {

    @NotBlank(message = "商户号不能为空")
    @Length(max = 50, message = "商户号长度不能超过50字符")
    private String userSign;

    @NotBlank(message = "商户订单号不能为空")
    @Length(max = 50, message = "商户订单号长度不能超过50字符")
    private String orderNumber;

    @NotBlank(message = "下单产品不能为空")
    @Pattern(regexp = "^(特快|普快)$", message = "下单产品必须为'特快'或'普快'")
    private String orderingProducts;

    @Length(max = 20, message = "发件手机长度不能超过20字符")
    private String sendMobilePhone = "";

    @Length(max = 20, message = "发件电话长度不能超过20字符")
    private String sendCall = "";

    @NotBlank(message = "发件姓名不能为空")
    @Length(max = 50, message = "发件姓名长度不能超过50字符")
    private String senderName;

    @Length(max = 100, message = "发件公司长度不能超过100字符")
    private String senderCompany = "";

    @NotBlank(message = "发件地址不能为空")
    @Length(max = 200, message = "发件地址长度不能超过200字符")
    private String senderAddress;

    @Length(max = 200, message = "发件地址1长度不能超过200字符")
    private String senderAddressOne = "";

    @NotBlank(message = "发件省/州不能为空")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "发件省/州必须为2位英文缩写")
    private String senderProvince;

    @NotBlank(message = "发件城市不能为空")
    @Length(max = 50, message = "发件城市长度不能超过50字符")
    private String senderCity;

    @NotBlank(message = "发件邮编不能为空")
    @Length(max = 20, message = "发件邮编长度不能超过20字符")
    private String senderPostalCode;

    @Length(max = 200, message = "发件街道/门牌长度不能超过200字符")
    private String senderStreet = "";

    @NotBlank(message = "发件地不能为空")
    @Length(max = 10, message = "发件地长度不能超过10字符")
    private String fromPlace;

    @NotBlank(message = "目的地不能为空")
    @Length(max = 10, message = "目的地长度不能超过10字符")
    private String destination;

    @NotBlank(message = "收件姓名不能为空")
    @Length(max = 50, message = "收件姓名长度不能超过50字符")
    private String recipientName;

    @Length(max = 100, message = "收件公司长度不能超过100字符")
    private String recipientCompany = "";

    @NotBlank(message = "收件地址不能为空")
    @Length(max = 200, message = "收件地址长度不能超过200字符")
    private String recipientAddress;

    @Length(max = 200, message = "收件地址1长度不能超过200字符")
    private String recipientAddressOne = "";

    @NotBlank(message = "收件省/州不能为空")
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "收件省/州必须为2位英文缩写")
    private String recipientProvince;

    @NotBlank(message = "收件城市不能为空")
    @Length(max = 50, message = "收件城市长度不能超过50字符")
    private String recipientCity;

    @NotBlank(message = "收件邮编不能为空")
    @Length(max = 20, message = "收件邮编长度不能超过20字符")
    private String recipientPostalCode;

    @Length(max = 200, message = "收件街道/门牌长度不能超过200字符")
    private String recipientStreet = "";

    @Length(max = 20, message = "收件电话长度不能超过20字符")
    private String recipientPhone = "";

    @Length(max = 20, message = "收件手机长度不能超过20字符")
    private String recipientMobilePhone = "";

    private List<LogisticsPackageDTO> packageList;

    private String transferNo;

    private BigDecimal freight;

    private String orderStatus;

    private String passengerOrder;

    private String quantityWeight;

    private BigDecimal weight;

    private String merchantNumber;

    private String pdfUrl;
}
