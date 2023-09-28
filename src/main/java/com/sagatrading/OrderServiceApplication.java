package com.sagatrading;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class  OrderServiceApplication {
	// Test pricing service
	public static void main(String[] args) throws JsonProcessingException {
//		ProductPricing pricingService = new MAPricingService();
//
//		Order myOrder = new Order();
//		myOrder.setProduct("IBM");
//		myOrder.setClientId(4);
//		myOrder.setType(OrderType.LIMIT);
//		myOrder.setQuantity(20000);
//		myOrder.setPrice(50);
//		myOrder.setSide(OrderSide.SELL);
//		pricingService.getExchangeAvailableForOrder(myOrder);

		SpringApplication.run(OrderServiceApplication.class, args);
	}
}
