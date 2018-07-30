package cn.kkl.mall.order.service;

import cn.kkl.mall.order.pojo.OrderInfo;
import cn.kkl.mall.pojo.E3Result;

public interface OrderSerivce {
	
	E3Result createOrder(OrderInfo orderInfo);

}
