package com.yang.service;

import com.yang.dto.LogisticsOrderDTO;
import com.yang.dto.PrintPdfsDTO;
import com.yang.vo.LogisticsOrderVO;
import com.yang.vo.PrintPdfsVO;
import jakarta.validation.Valid;

import java.io.IOException;

public interface LogisticsOrderService {
    /**
     * 新增物流订单
     * @param logisticsOrderDTO 物流订单DTO
     * @return 新增成功后的物流订单
     */
     LogisticsOrderVO addLogisticsOrder(LogisticsOrderDTO logisticsOrderDTO) throws IOException;

    PrintPdfsVO printPdf(@Valid PrintPdfsDTO printPdfsDTO);
}
