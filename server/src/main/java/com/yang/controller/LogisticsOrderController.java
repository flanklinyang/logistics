package com.yang.controller;

import com.yang.dto.LogisticsOrderDTO;
import com.yang.dto.PrintPdfsDTO;
import com.yang.result.Result;
import com.yang.vo.LogisticsOrderVO;
import com.yang.vo.PrintPdfsVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yang.service.LogisticsOrderService;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/devLogisticsOrder")

public class LogisticsOrderController {

    @Autowired
    private LogisticsOrderService logisticsOrderService;

    /**
     * 新增物流包裹
     * @param logisticsOrderDTO 物流订单DTO
     * @return 新增成功后的物流订单
     */
    @PostMapping("/addPlaceOrder")
    public Result<LogisticsOrderVO> addLogisticsOrder(@Valid @RequestBody LogisticsOrderDTO logisticsOrderDTO) throws IOException {
        LogisticsOrderVO logisticsOrderVO = logisticsOrderService.addLogisticsOrder(logisticsOrderDTO);
        return Result.success(logisticsOrderVO);
    }

     /**
     * 打印物流订单PDF
     * @param printPdfsDTO 打印PdfDTO
     * @return 打印成功后的PdfVO
     */
    @PostMapping("/printPdfApi")
    public Result<PrintPdfsVO> printPdf(@Valid @RequestBody PrintPdfsDTO printPdfsDTO) throws IOException {
        PrintPdfsVO printPdfsVO = logisticsOrderService.printPdf(printPdfsDTO);
        return Result.success(printPdfsVO);
    }
}
