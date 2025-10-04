package com.yang.exception;



/**
 * 订单号重复异常
 */

public class OrderNumberDuplicateKeyException extends RuntimeException {

    public OrderNumberDuplicateKeyException(String orderNumber) {
        super(String.format("订单号[%s]已存在,无法重复添加",orderNumber));
    }


}
