package com.yang.mapper;

import com.yang.entity.LogisticsOrder;
import com.yang.entity.LogisticsPackage;
import com.yang.entity.LogisticsProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogisticsOrderMapper {

        /**
         * 新增物流订单
         * @param logisticsOrder 物流订单实体
         * @return 新增成功后的物流订单实体
         */
        void insertLogisticsOrder(LogisticsOrder logisticsOrder);

        /**
         * 新增物流包裹
         * @param logisticsPackage 物流包裹实体
         */
        void insertLogisticsPackage(LogisticsPackage logisticsPackage);

        /**
         * 新增物流商品
         * @param logisticsProduct 物流商品实体
         */
        void insertLogisticsProduct(LogisticsProduct logisticsProduct);

}
