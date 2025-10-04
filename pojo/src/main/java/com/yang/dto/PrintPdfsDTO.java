package com.yang.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PrintPdfsDTO {

    @NotBlank(message = "商户号不能为空")
    @Length(max = 50, message = "商户号长度不能超过50字符")
    private String userSign;

    @NotBlank(message = "物流单号不能为空")
    private List<String> transferNos;
}
