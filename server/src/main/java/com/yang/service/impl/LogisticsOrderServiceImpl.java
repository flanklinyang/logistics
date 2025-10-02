package com.yang.service.impl;

import com.yang.dto.LogisticsOrderDTO;
import com.yang.dto.LogisticsPackageDTO;
import com.yang.dto.LogisticsProductDTO;
import com.yang.entity.LogisticsOrder;
import com.yang.entity.LogisticsPackage;
import com.yang.entity.LogisticsProduct;
import com.yang.mapper.LogisticsOrderMapper;
import com.yang.service.LogisticsOrderService;
import com.yang.vo.LogisticsOrderVO;
import com.yang.vo.LogisticsPackageVO;
import com.yang.vo.LogisticsProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class LogisticsOrderServiceImpl implements LogisticsOrderService {

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    /**
     * 新增物流订单
     * @param logisticsOrderDTO 物流订单DTO
     * @return 新增成功后的物流订单VO
     */
    @Override
    public LogisticsOrderVO addLogisticsOrder(LogisticsOrderDTO logisticsOrderDTO) {
        LogisticsOrder logisticsOrder = new LogisticsOrder();
        BeanUtils.copyProperties(logisticsOrderDTO,logisticsOrder);
        logisticsOrder.setFreight(BigDecimal.valueOf(10));
        logisticsOrder.setOrderStatus("派送中");
        logisticsOrder.setPassengerOrder(System.currentTimeMillis() + UUID.randomUUID().toString());

        int totalQuantity = 0;
        double totalHeavy = 0.0;
        for (LogisticsPackageDTO packageDTO : logisticsOrderDTO.getPackageList()) {
            for (LogisticsProductDTO productDTO : packageDTO.getSpecSubList()) {
                totalQuantity += productDTO.getNumber();
                totalHeavy += productDTO.getNumber() * Double.parseDouble(packageDTO.getPackageHeavy());
            }
        }
        logisticsOrder.setQuantityWeight(totalQuantity + "件/" + totalHeavy + "kg");
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (LogisticsPackageDTO packageDTO : logisticsOrderDTO.getPackageList()) {
            totalWeight = totalWeight.add(BigDecimal.valueOf(Double.parseDouble(packageDTO.getPackageHeavy())));
        }
        logisticsOrder.setWeight(totalWeight);
        logisticsOrder.setMerchantNumber(System.currentTimeMillis() + UUID.randomUUID().toString());
        logisticsOrder.setPdfUrl("https://www.baidu.com");
        String no=generateTransferNo();
        log.info("新增物流订单，订单号：{}",no);
        logisticsOrder.setTransferNo(no);
        logisticsOrderMapper.insertLogisticsOrder(logisticsOrder);
        LogisticsOrderVO logisticsOrderVO = new LogisticsOrderVO();
        BeanUtils.copyProperties(logisticsOrder,logisticsOrderVO);
        logisticsOrderVO.setFreight(logisticsOrder.getFreight().doubleValue());
        logisticsOrderVO.setWeight(logisticsOrder.getWeight().doubleValue());
        List<LogisticsPackageVO> logisticsPackageVOList = new ArrayList<>();
        for (LogisticsPackageDTO packageDTO : logisticsOrderDTO.getPackageList()) {
            LogisticsPackage logisticsPackage = new LogisticsPackage();
            BeanUtils.copyProperties(packageDTO,logisticsPackage);
            logisticsPackage.setOrderId(logisticsOrder.getId());
            logisticsOrderMapper.insertLogisticsPackage(logisticsPackage);
            LogisticsPackageVO logisticsPackageVO = new LogisticsPackageVO();
            BeanUtils.copyProperties(logisticsPackage,logisticsPackageVO);
            logisticsPackageVO.setPackageHeavy(Double.parseDouble(logisticsPackage.getPackageHeavy()));
            logisticsPackageVO.setPackageWidth(Double.parseDouble(logisticsPackage.getPackageWidth()));
            logisticsPackageVO.setPackageHigh(Double.parseDouble(logisticsPackage.getPackageHigh()));
            logisticsPackageVO.setPackageLength(Double.parseDouble(logisticsPackage.getPackageLength()));
            logisticsPackageVOList.add(logisticsPackageVO);
            List<LogisticsProductVO> logisticsProductVOList = new ArrayList<>();
            for (LogisticsProductDTO logisticsProductDTO : packageDTO.getSpecSubList()) {

                LogisticsProduct logisticsProduct = new LogisticsProduct();
                BeanUtils.copyProperties(logisticsProductDTO,logisticsProduct);
                logisticsProduct.setPackageId(logisticsPackage.getId());
                logisticsOrderMapper.insertLogisticsProduct(logisticsProduct);

                LogisticsProductVO logisticsProductVO = new LogisticsProductVO();
                BeanUtils.copyProperties(logisticsProduct,logisticsProductVO);
                logisticsProductVO.setPrice(logisticsProduct.getPrice().doubleValue());
                logisticsProductVO.setMoney(logisticsProduct.getMoney().doubleValue());
                logisticsProductVOList.add(logisticsProductVO);
            }
            logisticsPackageVO.setLogisticsProductVOList(logisticsProductVOList);
        }
        logisticsOrderVO.setPackageList(logisticsPackageVOList);
        return logisticsOrderVO;
    }

    /**
     * 生成物流订单号
     * @return 物流订单号
     */
    private String generateTransferNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString();
    }
}
