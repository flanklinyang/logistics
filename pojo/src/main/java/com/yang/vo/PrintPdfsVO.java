package com.yang.vo;

import lombok.Data;

import java.util.List;

@Data
public class PrintPdfsVO {

    private String pdfUrl;

    private List<String> errorTransferNos;

}
