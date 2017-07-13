package org.cijug;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class CoffeeOrderSaver {

    private AmazonDynamoDB dynamoDBClient;
    private ObjectMapper objectMapper;

    public CoffeeOrderSaver() {
        DefaultAWSCredentialsProviderChain credentials = new DefaultAWSCredentialsProviderChain();
        ClientConfiguration dynamoConfig = new ClientConfiguration();
        dynamoConfig.setProtocol(Protocol.HTTP);

        dynamoDBClient = AmazonDynamoDBAsyncClient.builder().withCredentials(credentials).withClientConfiguration(dynamoConfig).build();
        objectMapper = new ObjectMapper();

        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String handleRequest(KinesisEvent orderEvent, Context context) throws IOException {
        context.getLogger().log("Saver received records, parsing them now.");
        return parseEventRecords(orderEvent, context);
    }

    private String parseEventRecords(KinesisEvent orderEvent, Context context) {
        return orderEvent.getRecords().stream().map(record -> {
            try {
                CoffeeOrderResponse completedOrder = objectMapper.readerFor(CoffeeOrderResponse.class).readValue(record.getKinesis().getData().array());
                context.getLogger().log(String.format("Saving order for %s", completedOrder.getOriginalRequest().getCustomerName()));
                return handleRecord(completedOrder);
            } catch (IOException e) {
                context.getLogger().log(e.getMessage());
                return "PARSE FAIL";
            }
        }).reduce("", (acc, ele) -> acc + ele );
    }

    private String handleRecord(CoffeeOrderResponse completedOrder) {
        Map<String, AttributeValue> orderToSave = new HashMap<>();
        orderToSave.put("customerName", new AttributeValue(completedOrder.getOriginalRequest().getCustomerName()));
        orderToSave.put("orderInstructions", new AttributeValue(completedOrder.getOriginalRequest().getOrderInstructions()));
        orderToSave.put("cost", new AttributeValue().withN(completedOrder.getCost().toString()));

        PutItemRequest putRequest = new PutItemRequest("coffeeOrders", orderToSave);
        PutItemResult putResult = dynamoDBClient.putItem(putRequest);

        return putResult.getSdkResponseMetadata().toString();
    }
}
