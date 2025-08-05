package ca.devign.orderstream.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
    
    @Value("${aws.region}")
    private String region;
    
    @Value("${aws.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aws.secret-access-key:}")
    private String secretAccessKey;
    
    @Bean
    public AWSLambda lambdaClient() {
        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                .withRegion(region);
        
        if (!accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        
        return builder.build();
    }
    
    @Bean
    public AmazonS3 s3Client() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withRegion(region);
        
        if (!accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        
        return builder.build();
    }
    
    @Bean
    public AmazonSNS snsClient() {
        AmazonSNSClientBuilder builder = AmazonSNSClientBuilder.standard()
                .withRegion(region);
        
        if (!accessKeyId.isEmpty() && !secretAccessKey.isEmpty()) {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        
        return builder.build();
    }
} 