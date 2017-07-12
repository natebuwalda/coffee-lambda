package org.cijug;


public class CoffeeOrderResponse {

    private CoffeeOrderRequest originalRequest;
    private MadeCoffee coffee;
    private Double cost;

    public CoffeeOrderRequest getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest(CoffeeOrderRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    public MadeCoffee getCoffee() {
        return coffee;
    }

    public void setCoffee(MadeCoffee coffee) {
        this.coffee = coffee;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
