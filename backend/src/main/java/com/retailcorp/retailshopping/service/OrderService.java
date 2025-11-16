package com.retailcorp.retailshopping.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.entity.CustomerEvent;
import com.retailcorp.retailshopping.entity.Order;
import com.retailcorp.retailshopping.entity.OrderItem;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerEventRepository eventRepository;

    @Autowired
    private CustomerRepository customerRepository;

public Order placeOrder(Order order) {
    order.setOrderDate(LocalDateTime.now());
    order.setOrderStatus("Processing");

    // Ensure each item points back to this order
    if (order.getItems() != null) {
        order.getItems().forEach(item -> item.setOrder(order));
    }

    // Save the order and cascade items
    orderRepository.save(order);

        // Update customer lifetime value
    Customer customer = customerRepository.findById(order.getCustomerId()).orElseThrow();
    double orderTotal = order.getTotalAmount() != null ? order.getTotalAmount() : 
                        order.getItems().stream().mapToDouble(OrderItem::getLineTotal).sum();
    customer.setLifetimeValue(customer.getLifetimeValue() + orderTotal);
    customerRepository.save(customer);

    // Log purchase event
    CustomerEvent event = new CustomerEvent();
    event.setCustomerId(order.getCustomerId());
    event.setEventTimestamp(LocalDateTime.now());
    event.setEventData("{\"event_type\":\"purchase\",\"order_id\":" + order.getOrderId() + "}");
    eventRepository.save(event);

    return order;
}

public List<Order> findByCustomerId(Long customerId) {
    // TODO Auto-generated method stub
    return orderRepository.findByCustomerId(customerId);
    // throw new UnsupportedOperationException("Unimplemented method 'findByCustomerId'");
}

}