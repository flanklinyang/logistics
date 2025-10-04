package com.yang.mapper;

import com.yang.entity.LogisticsOrder;
import com.yang.entity.LogisticsPackage;
import com.yang.entity.LogisticsProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogisticsOrderMapper {

        /**
         * 新增物流订单
         * @param logisticsOrder 物流订单实体
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

        /**
         *  查询订单号是否已存在
         * @param orderNumber 订单号
         * @return Integer
         */
        @Select("select count(1) from logistics_order WHERE order_number = #{orderNumber}")
        Integer countByOrderNumber(String orderNumber);

        /**
         * 根据商户号查询物流订单
         * @param userSign 商户号
         * @return 物流订单详情
         */
        @Select("select * from logistics_order WHERE user_sign = #{userSign}")
        List<LogisticsOrder> selectLogisticsOrderByUserSign(String userSign);
}
