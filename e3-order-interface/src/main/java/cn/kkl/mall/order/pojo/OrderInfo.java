package cn.kkl.mall.order.pojo;

import java.io.Serializable;
import java.util.List;

import cn.kkl.mall.pojo.TbOrder;
import cn.kkl.mall.pojo.TbOrderItem;
import cn.kkl.mall.pojo.TbOrderShipping;

public class OrderInfo extends TbOrder implements Serializable{

	/**
	 * need network transmit
	 */
	private static final long serialVersionUID = 8535600628521597662L;
	
	private List<TbOrderItem> orderItems;
	
	private TbOrderShipping orderShipping;

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}

	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	
	

}
