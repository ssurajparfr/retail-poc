package com.retailcorp.retailshopping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.retailcorp.retailshopping.dto.OrderItemRequest;
import com.retailcorp.retailshopping.dto.OrderItemResponse;
import com.retailcorp.retailshopping.dto.OrderRequest;
import com.retailcorp.retailshopping.dto.OrderResponse;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.entity.CustomerEvent;
import com.retailcorp.retailshopping.entity.Order;
import com.retailcorp.retailshopping.entity.OrderItem;
import com.retailcorp.retailshopping.exception.InvalidOrderException;
import com.retailcorp.retailshopping.exception.ResourceNotFoundException;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CustomerRepository customerRepository;
    private final CustomerEventRepository eventRepository;

    // Checkout method
    public OrderResponse placeOrder(OrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item.");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + request.getCustomerId())
                );

        Order order = toEntity(request);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("Processing");

        // Calculate item totals
        order.getItems().forEach(item -> {
            item.setOrder(order);

            if (item.getQuantity() <= 0) {
                throw new InvalidOrderException("Item quantity must be greater than zero.");
            }

            Double unitPrice = productService.getProductById(item.getProductId()).getUnitPrice();
            if (unitPrice == null) {
                throw new InvalidOrderException("Product price missing for product ID: " + item.getProductId());
            }
            item.setUnitPrice(unitPrice);
            item.setLineTotal(unitPrice * item.getQuantity());
        });

        // Calculate total order amount
        double totalAmount = order.getItems().stream()
                .mapToDouble(OrderItem::getLineTotal)
                .sum();

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update customer lifetime value safely
        double currentLtv = customer.getLifetimeValue() == null ? 0.0 : customer.getLifetimeValue();
        customer.setLifetimeValue(currentLtv + totalAmount);
        customerRepository.save(customer);

        // Log event
        CustomerEvent event = new CustomerEvent();
        event.setCustomerId(savedOrder.getCustomerId());
        event.setEventTimestamp(LocalDateTime.now());
        event.setEventData(
                "{\"event_type\":\"purchase\",\"order_id\":" + savedOrder.getOrderId() + "}"
        );
        eventRepository.save(event);

        return toResponse(savedOrder);
    }

    // Get all orders for a customer
    public List<OrderResponse> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Convert DTO -> Entity
    private Order toEntity(OrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        order.setItems(request.getItems().stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList()));
        return order;
    }

    private OrderItem toOrderItem(OrderItemRequest itemReq) {
        OrderItem item = new OrderItem();
        item.setProductId(itemReq.getProductId());
        item.setQuantity(itemReq.getQuantity());
        return item;
    }

    // Convert Entity -> DTO
    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .items(order.getItems().stream()
                        .map(this::toOrderItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .productName(productService.getProductById(item.getProductId()).getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .discountPercent(item.getDiscountPercent())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
