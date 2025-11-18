package com.retailcorp.retailshopping.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.retailcorp.retailshopping.dto.RegisterRequest;
import com.retailcorp.retailshopping.entity.Customer;
import com.retailcorp.retailshopping.entity.CustomerEvent;
import com.retailcorp.retailshopping.entity.Order;
import com.retailcorp.retailshopping.entity.Product;
import com.retailcorp.retailshopping.service.CustomerService;
import com.retailcorp.retailshopping.service.EventService;
import com.retailcorp.retailshopping.service.OrderService;
import com.retailcorp.retailshopping.service.ProductService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EventService eventService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody RegisterRequest customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping("/customers/search")
    public java.util.List<Customer> searchCustomers(@RequestParam String email) {
        return customerService.searchByEmail(email);
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/customers/{id}/events")
    public List<CustomerEvent> getCustomerEvents(@PathVariable Long id) {
        return eventService.getEventsForCustomer(id);
    }


    @PostMapping("/checkout")
    public Order checkout(@RequestBody Order order) {
        return orderService.placeOrder(order);
    }

    @PostMapping("/events")
    public CustomerEvent logEvent(@RequestBody CustomerEvent event) {
        return eventService.logEvent(event);
    }

    @GetMapping("/orders/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Long customerId) {
        return orderService.findByCustomerId(customerId);
    }
}
