package com.yang.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class LogisticsProductDTO {

    @NotBlank(message = "商品中文品名不能为空")
    @Length(max = 100, message = "中文品名长度不能超过100字符")
    private String chineseName;

    @NotBlank(message = "商品英文名称不能为空")
    @Length(max = 100, message = "英文名称长度不能超过100字符")
    private String englishName;

    @NotBlank(message = "商品币种不能为空")
    @Pattern(regexp = "^(CNY|USD|HKD)$", message = "币种仅支持CNY、USD、HKD")
    private String specie;

    @NotNull(message = "商品数量不能为空")
    @DecimalMin(value = "1", message = "商品数量不能小于1")
    private Integer number;

    @NotNull(message = "商品销售金额不能为空")
    @DecimalMin(value = "0.00", message = "销售金额不能小于0")
    private BigDecimal money;

    @NotNull(message = "商品申报价格不能为空")
    @DecimalMin(value = "0.00", message = "申报价格不能小于0")
    private BigDecimal price;

    @NotBlank(message = "商品申请币种不能为空")
    @Pattern(regexp = "^(CNY|USD|HKD)$", message = "申请币种仅支持CNY、USD、HKD")
    private String currency;

    @NotBlank(message = "商品规格不能为空")
    @Length(max = 100, message = "商品规格长度不能超过100字符")
    private String specifications;
}
