package com.yang.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
public class LogisticsPackage {

    private Long id;

    private Long orderId;

    @Length(max = 200, message = "包裹说明长度不能超过200字符")
    private String packageExplain = "";

    @NotBlank(message = "包裹宽不能为空")
    @Length(max = 20, message = "包裹宽长度不能超过20字符")
    private String packageWidth;

    @NotBlank(message = "包裹高不能为空")
    @Length(max = 20, message = "包裹高长度不能超过20字符")
    private String packageHigh;

    @NotBlank(message = "包裹长不能为空")
    @Length(max = 20, message = "包裹长长度不能超过20字符")
    private String packageLength;

    @NotBlank(message = "包裹实重不能为空")
    @Length(max = 20, message = "包裹实重长度不能超过20字符")
    private String packageHeavy;

    @NotBlank(message = "包裹下至少需包含1个商品")
    private List<LogisticsProduct> specSubList;

    private Date createTime;

    private Date updateTime;

}
