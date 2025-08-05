package ca.devign.orderstream.lambda;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceLambdaService {
    
    private final AWSLambda lambdaClient;
    
    @Value("${aws.lambda.function-name}")
    private String functionName;
    
    public void generateInvoice(Long orderId) {
        try {
            String payload = String.format("{\"orderId\": %d}", orderId);
            ByteBuffer payloadBytes = ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8));
            
            InvokeRequest invokeRequest = new InvokeRequest()
                    .withFunctionName(functionName)
                    .withPayload(payloadBytes);
            
            InvokeResult result = lambdaClient.invoke(invokeRequest);
            
            if (result.getStatusCode() == 200) {
                log.info("Successfully triggered invoice generation for order ID: {}", orderId);
            } else {
                log.error("Failed to trigger invoice generation for order ID: {}. Status: {}", 
                         orderId, result.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error triggering invoice generation for order ID: {}", orderId, e);
        }
    }
} 