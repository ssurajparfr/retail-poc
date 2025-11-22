package com.retailcorp.retailshopping.unit.service;

import com.retailcorp.retailshopping.dto.OrderItemRequest;
import com.retailcorp.retailshopping.dto.OrderRequest;
import com.retailcorp.retailshopping.dto.OrderResponse;
import com.retailcorp.retailshopping.dto.ProductResponse;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.entity.Order;
import com.retailcorp.retailshopping.exception.InvalidOrderException;
import com.retailcorp.retailshopping.exception.ResourceNotFoundException;
import com.retailcorp.retailshopping.repository.CustomerEventRepository;
import com.retailcorp.retailshopping.repository.CustomerRepository;
import com.retailcorp.retailshopping.repository.OrderRepository;
import com.retailcorp.retailshopping.service.OrderService;
import com.retailcorp.retailshopping.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerEventRepository eventRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private ProductResponse product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setLifetimeValue(100.0);

        product =  ProductResponse.builder()
        .productId(10L)
        .productName("Test Product")
        .unitPrice(25.0)
        .build();
    }

    @Test
    void placeOrder_throwsWhenItemsNull() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setItems(null);

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Order must have at least one item.");
    }

    @Test
    void placeOrder_throwsWhenItemsEmpty() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setItems(new ArrayList<>());

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Order must have at least one item.");
    }

    @Test
    void placeOrder_throwsWhenCustomerNotFound() {
        OrderItemRequest itemRequest = new OrderItemRequest(10L,2);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(999L);
        request.setItems(List.of(itemRequest));

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void placeOrder_throwsWhenQuantityZeroOrNegative() {
        OrderItemRequest itemRequest = new OrderItemRequest(10L,0);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setItems(List.of(itemRequest));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Item quantity must be greater than zero.");
    }

    @Test
    void placeOrder_throwsWhenQuantityNegative() {
        OrderItemRequest itemRequest = new OrderItemRequest(10L,-5);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setItems(List.of(itemRequest));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Item quantity must be greater than zero.");
    }

    @Test
    void placeOrder_throwsWhenProductPriceNull() {
        OrderItemRequest itemRequest = new OrderItemRequest(10L,2);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setItems(List.of(itemRequest));

        ProductResponse productWithNullPrice =  ProductResponse.builder()
        .productId(10L)
        .unitPrice(null) // Null price
        .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productService.getProductById(10L)).thenReturn(productWithNullPrice);

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessageContaining("Product price missing");
    }

    @Test
    void placeOrder_successWithNullLifetimeValue() {
        customer.setLifetimeValue(null);  // Test null LTV branch

        OrderItemRequest itemRequest = new OrderItemRequest(10L,2);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setPaymentMethod("Credit Card");
        request.setShippingAddress("123 Main St");
        request.setItems(List.of(itemRequest));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productService.getProductById(10L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setOrderId(100L);
            return o;
        });

        OrderResponse response = orderService.placeOrder(request);

        assertThat(response.getOrderId()).isEqualTo(100L);
        assertThat(response.getTotalAmount()).isEqualTo(50.0);  // 25.0 * 2
        assertThat(customer.getLifetimeValue()).isEqualTo(50.0);
    }

    @Test
    void placeOrder_successfulOrder() {
        OrderItemRequest itemRequest = new OrderItemRequest(10L, 3);

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setPaymentMethod("PayPal");
        request.setShippingAddress("456 Oak Ave");
        request.setItems(List.of(itemRequest));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productService.getProductById(10L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setOrderId(200L);
            return o;
        });

        OrderResponse response = orderService.placeOrder(request);

        assertThat(response.getOrderId()).isEqualTo(200L);
        assertThat(response.getOrderStatus()).isEqualTo("Processing");
        assertThat(response.getTotalAmount()).isEqualTo(75.0);  // 25.0 * 3
    }

    @Test
    void findByCustomerId_returnsOrders() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomerId(1L);
        order.setOrderStatus("Delivered");
        order.setItems(new ArrayList<>());

        when(orderRepository.findByCustomerId(1L)).thenReturn(List.of(order));

        List<OrderResponse> responses = orderService.findByCustomerId(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getOrderId()).isEqualTo(1L);
    }
}