package ca.devign.orderstream.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceQueueService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${redis.queue.order-complete}")
    private String orderCompleteQueue;
    
    public void publishOrderComplete(Long orderId) {
        try {
            redisTemplate.opsForList().rightPush(orderCompleteQueue, orderId);
            log.info("Published order complete event for order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish order complete event for order ID: {}", orderId, e);
            // Don't throw exception to avoid affecting the main order flow
        }
    }
} 