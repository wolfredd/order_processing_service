package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.activity.Activity;
import com.sagatrading.contract.ExchangeConfigurator;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.contract.ProductDataManager;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.dto.OrderDto;
import com.sagatrading.exception.FailedOrderException;
import com.sagatrading.exception.InvalidOrderException;
import com.sagatrading.exception.OrderNotFoundException;
import com.sagatrading.model.LogType;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderStatus;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.model.dto.OrderExchangeInfo;
import com.sagatrading.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.*;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ExchangeRequester exchangeService;

    @Autowired
    ReportingService reportingService;

    @Autowired
    ProductDataManager productPricing;

    @Autowired
    OrderValidationService orderValidationService;

    @Autowired
    ExchangeConfigurator exchangeConfigService;

    @Autowired
    OrderSplitter orderSplitter;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public OrderService(
            OrderRepository orderRepository,
            MAExchangeRequester exchangeService,
            ReportingService reportingService,
            ProductDataManager productPricing,
            OrderValidationService orderValidationService,
            OrderSplitter orderSplitter,
            ExchangeConfigurator exchangeConfigService
    ) {
        this.orderRepository = orderRepository;
        this.exchangeService = exchangeService;
        this.reportingService = reportingService;
        this.productPricing = productPricing;
        this.orderValidationService = orderValidationService;
        this.orderSplitter = orderSplitter;
        this.exchangeConfigService = exchangeConfigService;
    }

    public UUID processOrder(Order order) {
        // Get available exchanges
        List<UUID> orderIDs = new ArrayList<>();
        orderValidationService.validateOrder(order);

        List<ExchangeDto> exchanges = productPricing.getExchangesAvailableForOrder(order);
        UUID referenceId = UUID.randomUUID();
        try {
            List<OrderExchangeInfo> splitOrders = orderSplitter.splitOrder(order, exchanges);
            Boolean isMultiLeg = splitOrders.size() > 1;
            for (OrderExchangeInfo orderExchangeInfo : splitOrders) {
                System.out.println("Placing order: " + orderExchangeInfo);
                UUID orderId = createOrder(orderExchangeInfo.getOrder(), orderExchangeInfo.getExchange(), referenceId, isMultiLeg);
                System.out.println("Order id: " + orderId + ", Exchange name: " + orderExchangeInfo.getExchange().getExchangeName());
                orderIDs.add(orderId);
            }
        } catch (JsonProcessingException ex) {
            // Todo: Create an exception for - Order could not be split
            throw new FailedOrderException("Order could not be processed");

            // Log errors to DB
        }
        // Log orders

        Activity activity = new Activity(order.getClientId(),"Client "+ order.getClientId() + " has their order being processed ");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serializedActivity =objectMapper.writeValueAsString(activity);
            rabbitTemplate.convertAndSend("activities.queue", serializedActivity);
            System.out.println(serializedActivity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return referenceId;
    }

    public UUID createOrder(Order order, ExchangeDto exchange, UUID referenceCode, boolean isMultiLeg) {
        UUID orderId;
        try {
            orderId = sendOrderToExchange(order, exchange);
            order.setId(orderId);
            order.setStatus(OrderStatus.PENDING);
            order.setReferenceCode(referenceCode);
            order.setMultiLeg(isMultiLeg);
            order.setExchange(exchange.getExchangeName());

            try {
                orderRepository.save(order);
            } catch (Exception ex) {
                // Todo: Log error - failed to save order in DB
                System.out.println(ex.getMessage());
            }
            reportingService.queueLog(LogType.CREATE_ORDER, order);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new InvalidOrderException(ex.getMessage());
        }
        Activity activity = new Activity(order.getClientId(),"Client "+ order.getClientId() + " created an order ");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serializedActivity =objectMapper.writeValueAsString(activity);
            rabbitTemplate.convertAndSend("activities.queue", serializedActivity);
            System.out.println(serializedActivity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return orderId;
    }

    public UUID updateOrder(Order order, UUID orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        if (existingOrder.isEmpty()) throw new OrderNotFoundException("Order could not be found");
        ExchangeDto exchange = exchangeConfigService.getExchangeByName("MAL1");
        exchangeService.updateOrder(order, orderId, exchange);
        order.setId(orderId);
        orderRepository.save(order);

        Activity activity = new Activity(order.getClientId(),"Client "+ order.getClientId() + " updated their order ");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serializedActivity =objectMapper.writeValueAsString(activity);
            rabbitTemplate.convertAndSend("activities.queue", serializedActivity);
            System.out.println(serializedActivity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public UUID sendOrderToExchange(Order order, ExchangeDto exchange) {
        String response = exchangeService.submitOrder(order, exchange);

        Activity activity = new Activity(order.getClientId(),"Order " + order.getId() + " for client "+ order.getClientId() + " has been submitted to exchange " + exchange.getExchangeName());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serializedActivity =objectMapper.writeValueAsString(activity);
            rabbitTemplate.convertAndSend("activities.queue", serializedActivity);
            System.out.println(serializedActivity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return UUID.fromString(response.replaceAll("\"", ""));
    }

    public List<Order> getOrdersByClientId(int clientId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Order> orders = orderRepository.findByClientId(clientId, sort);

        Activity activity = new Activity("Client "+ clientId + " has their order being processed ");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serializedActivity =objectMapper.writeValueAsString(activity);
            rabbitTemplate.convertAndSend("activities.queue", serializedActivity);
            System.out.println(serializedActivity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }

    public List<OrderBookItem> getMarketOrders(String product, String status, String exchangeName) {
        ExchangeDto exchange = exchangeConfigService.getExchangeByName(exchangeName);
        try {
            if (Objects.equals(status, "open"))
                return exchangeService.getOpenOrders(product, exchange);
            if (Objects.equals(status, "closed"))
                return exchangeService.getClosedOrders(product, exchange);

        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Could not get requested orders");
        }
        return null;
    }

    public List<Double> getPriceHistory(String product, String exchangeName) {
        ExchangeDto exchange = exchangeConfigService.getExchangeByName(exchangeName);
        try {
            List<OrderBookItem> orderBook = exchangeService.getProductOrderBook(product, exchange);
            List<Double> priceHistory = orderBook
                    .stream()
                    .map(OrderBookItem::getPrice)
                    .filter(price -> price > 0)
                    .toList();
            return priceHistory;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Could not get requested orders");
        }
    }


    public void updateOrder() {

    }

    public boolean cancelOrder(UUID orderID) {
        Optional<Order> optionalOrder = orderRepository.findById(orderID);

        if (optionalOrder.isEmpty()) throw new RuntimeException("Order not found");
        Order order = optionalOrder.get();
        boolean isCancellationSuccessful = exchangeService
                .cancelOrder(
                        orderID, exchangeConfigService.getExchangeByName(order.getExchange())
                );
        if (isCancellationSuccessful) {
            // Mark as cancelled
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            Activity activity = new Activity(order.getClientId(),"Client "+ order.getClientId() + " cancelled order " + order.getId());

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String serializedActivity =objectMapper.writeValueAsString(activity);
                rabbitTemplate.convertAndSend("activities.queue", serializedActivity);
                System.out.println(serializedActivity);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return isCancellationSuccessful;
    }


    public List getPendingOrders(){
        List<Order> pendingOrders = orderRepository.findAllByStatus(OrderStatus.PENDING);
        return pendingOrders;
    }

    public List getCancelledOrders(){
        List<Order> cancelledOrders = orderRepository.findAllByStatus(OrderStatus.CANCELLED);
        return cancelledOrders;
    }

    public List getFilledOrders(){
        List<Order> filledOrders = orderRepository.findAllByStatus(OrderStatus.FILLED);
        return filledOrders;
    }

    public List getFailedOrders(){
        List<Order> failedOrders = orderRepository.findAllByStatus(OrderStatus.FAILED);
        return failedOrders;
    }
    //lol

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public void getProductDetails() {
//        exchangeService.getProductDetails(product);
    }

    public void getOrderDetails() {

    }
}
