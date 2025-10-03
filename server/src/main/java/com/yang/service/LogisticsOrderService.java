package com.yang.service;

import com.yang.dto.LogisticsOrderDTO;
import com.yang.dto.LogisticsPackageDTO;
import com.yang.entity.LogisticsOrder;
import com.yang.vo.LogisticsOrderVO;

import java.io.IOException;

public interface LogisticsOrderService {
    /**
     * 新增物流订单
     * @param logisticsOrderDTO 物流订单DTO
     * @return 新增成功后的物流订单
     */
     LogisticsOrderVO addLogisticsOrder(LogisticsOrderDTO logisticsOrderDTO) throws IOException;
}
