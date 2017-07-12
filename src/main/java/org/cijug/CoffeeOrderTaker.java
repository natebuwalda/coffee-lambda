package org.cijug;


import com.amazonaws.services.lambda.runtime.Context;

public class CoffeeOrderTaker {

    public CoffeeOrderResponse handleRequest(CoffeeOrderRequest request, Context lambdaContext) {
        lambdaContext.getLogger().log(String.format("Making coffee for %s", request.getCustomerName()));

        CoffeeOrderResponse response = new CoffeeOrderResponse();
        response.setOriginalRequest(request);

        MadeCoffee coffee = new MadeCoffee();
        coffee.setInstructions(request.getOrderInstructions());
        coffee.setDecaf(false);
        coffee.setHasCream(true);
        response.setCoffee(coffee);

        return response;
    }

}
