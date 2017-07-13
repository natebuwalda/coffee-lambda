package org.cijug;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;

public class CoffeeOrderTaker {

    private AmazonKinesis client;
    private ObjectMapper objectMapper;

    public CoffeeOrderTaker() {
        DefaultAWSCredentialsProviderChain credentials = new DefaultAWSCredentialsProviderChain();
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        client = AmazonKinesisClient.builder().withCredentials(credentials)
                .withClientConfiguration(clientConfig)
                .build();
        objectMapper = new ObjectMapper();
    }

    public CoffeeOrderResponse handleRequest(CoffeeOrderRequest request, Context context) throws JsonProcessingException {
        context.getLogger().log(String.format("Making coffee for %s", request.getCustomerName()));

        CoffeeOrderResponse response = new CoffeeOrderResponse();
        response.setOriginalRequest(request);

        MadeCoffee coffee = new MadeCoffee();
        coffee.setInstructions(request.getOrderInstructions());
        coffee.setDecaf(false);
        coffee.setHasCream(true);

        response.setCoffee(coffee);
        response.setCost(10.0d);

        PutRecordRequest recordRequest = new PutRecordRequest();
        recordRequest.setStreamName("coffeeOrders");
        recordRequest.setPartitionKey("1");
        recordRequest.setData(ByteBuffer.wrap(objectMapper.writerFor(CoffeeOrderResponse.class).writeValueAsBytes(response)));
        client.putRecord(recordRequest);

        context.getLogger().log("Made coffee; cost was " + response.getCost());
        return response;
    }

}
