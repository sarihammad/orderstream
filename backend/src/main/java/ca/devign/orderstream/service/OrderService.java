package ca.devign.orderstream.service;

import ca.devign.orderstream.dto.OrderItemRequest;
import ca.devign.orderstream.dto.OrderRequest;
import ca.devign.orderstream.dto.OrderResponse;
import ca.devign.orderstream.entity.Order;
import ca.devign.orderstream.entity.OrderItem;
import ca.devign.orderstream.entity.Product;
import ca.devign.orderstream.entity.User;
import ca.devign.orderstream.queue.InvoiceQueueService;
import ca.devign.orderstream.repository.OrderRepository;
import ca.devign.orderstream.repository.ProductRepository;
import ca.devign.orderstream.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final InvoiceQueueService invoiceQueueService;
    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate all products exist and have sufficient stock
        List<Long> productIds = request.getItems().stream()
                .map(OrderItemRequest::getProductId)
                .collect(Collectors.toList());
        
        List<Product> products = productRepository.findByIdsWithLock(productIds);
        
        if (products.size() != productIds.size()) {
            throw new RuntimeException("Some products not found");
        }
        
        // Check stock availability
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            Integer requestedQuantity = request.getItems().get(i).getQuantity();
            
            if (!product.hasStock(requestedQuantity)) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }
        
        // Create order
        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(java.math.BigDecimal.ZERO)
                .build();
        
        // Add order items and decrease stock
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            OrderItemRequest itemRequest = request.getItems().get(i);
            
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();
            
            order.addOrderItem(orderItem);
            product.decreaseStock(itemRequest.getQuantity());
            productRepository.save(product);
        }
        
        order.calculateTotal();
        Order savedOrder = orderRepository.save(order);
        
        // Trigger async invoice generation
        invoiceQueueService.publishOrderComplete(savedOrder.getId());
        
        return OrderResponse.fromOrder(savedOrder);
    }
    
    public Page<OrderResponse> getUserOrders(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return orderRepository.findByUser(user, pageable)
                .map(OrderResponse::fromOrder);
    }
    
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderResponse::fromOrder);
    }
    
    public Page<OrderResponse> getOrdersByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(OrderResponse::fromOrder);
    }
    
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user can access this order (owner or admin)
        if (!order.getUser().getId().equals(user.getId()) && 
            !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Access denied");
        }
        
        return OrderResponse.fromOrder(order);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        return OrderResponse.fromOrder(savedOrder);
    }
} 