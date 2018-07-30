package cn.kkl.mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.kkl.mall.mapper.TbOrderItemMapper;
import cn.kkl.mall.mapper.TbOrderMapper;
import cn.kkl.mall.mapper.TbOrderShippingMapper;
import cn.kkl.mall.order.pojo.OrderInfo;
import cn.kkl.mall.order.service.OrderSerivce;
import cn.kkl.mall.pojo.E3Result;
import cn.kkl.mall.pojo.TbOrder;
import cn.kkl.mall.pojo.TbOrderItem;
import cn.kkl.mall.pojo.TbOrderShipping;
import cn.kkl.mall.service.JedisClient;
import cn.kkl.mall.utils.IDUtils;
import javassist.expr.NewArray;

@Service
public class OderSerivceImpl implements OrderSerivce {
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;
	
	@Value("${GEN_ORDER_ID_KEY}")
	private String orderIdGenerator;
	
	@Value("${ORDER_INITIAL_VLAUE}")
	private String orderInitialValue;
	
	@Value("${ORDER_DETAIL_ID_GEN}")
	private String orderItemIdGenerator;
	
	/* create order logic:
	 * 1. generate orderId use by redis incr mehtod
	 * 2. complete tbOrder instance attributes and insert tables tb_order one record
	 * 2. polling List<TbOrderItem> then complete tbOrderItem attributes and insert table Tb_order_item one record very one tbOderItem instance.
	 * 3. complete tbOrderShopping instance attributes and insert table tbOrderShopping one record
	 * 4. return use E3Result wrapped orderId
	 */
	@Override
	public E3Result createOrder(OrderInfo orderInfo) {
		if (!jedisClient.exists(orderIdGenerator)) {
			jedisClient.set(orderIdGenerator, orderInitialValue);
		}
		String orderId = jedisClient.incr(orderIdGenerator).toString();
		TbOrder order = new TbOrder();
		Date date = new Date();
		order.setOrderId(orderId);
		order.setCreateTime(date);
		order.setUpdateTime(date);
		//status: 1-unpaid 2-paid  3-not shipped 4-shipped 5-deal success 6-deal close
		order.setStatus(1);
		order.setBuyerNick(orderInfo.getBuyerNick());
		order.setUserId(orderInfo.getUserId());
		order.setPayment(orderInfo.getPayment());
		order.setPaymentType(orderInfo.getPaymentType());
		order.setPostFee(orderInfo.getPostFee());
		orderMapper.insertSelective(order);
		
		List<TbOrderItem> orderItems = orderInfo.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			tbOrderItem.setId(jedisClient.incr(orderItemIdGenerator).toString());
			tbOrderItem.setOrderId(orderId);
			orderItemMapper.insertSelective(tbOrderItem);
		}
		
		TbOrderShipping shipping = orderInfo.getOrderShipping();
		shipping.setCreated(date);
		shipping.setOrderId(orderId);
		shipping.setUpdated(date);
		orderShippingMapper.insertSelective(shipping);
		return E3Result.ok(orderId);
	}

}

