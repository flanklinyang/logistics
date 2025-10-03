package com.yang.dto;

import java.util.List;
import lombok.Data;

@Data
public class PrintPdfDTO {

    private String userSign;

    private List<String> transferNos;
}
