package com.programming.orderservice.service;

import com.programming.orderservice.dto.InventoryResponse;
import com.programming.orderservice.dto.OrderLineItemsDto;
import com.programming.orderservice.dto.OrderRequest;
import com.programming.orderservice.event.OrderPlacedEvent;
import com.programming.orderservice.model.Order;
import com.programming.orderservice.model.OrderLineItems;
import com.programming.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.brave.bridge.BraveTracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;


        this.kafkaTemplate = kafkaTemplate;
    }

    public String placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setOrderLineItemsList(orderRequest.getOrderLineItemsDtoList()
                .stream().
                map(this::mapToOrderLineItems).toList());

            List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
            //call inventory service and place order if it is in inventory
            InventoryResponse[] inventoryResponses = webClientBuilder.build().get().uri("http://inventory-service//api/inventory",
                            UriBuilder-> UriBuilder.queryParam("skuCode",skuCodes).build()).retrieve()
                    .bodyToMono(InventoryResponse[].class).block();
            boolean result = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
            if(result){
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(
                        order.getOrderNumber()));
                return "Order Placed Successfully";
            }
            else{
                throw new IllegalArgumentException("Product is not available, pls try again later");
            }




    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());

        return orderLineItems;

    }
}
