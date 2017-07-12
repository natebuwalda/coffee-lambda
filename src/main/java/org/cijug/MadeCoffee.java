package org.cijug;

public class MadeCoffee {

    private String instructions;
    private Boolean isDecaf;
    private Boolean hasCream;

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Boolean getDecaf() {
        return isDecaf;
    }

    public void setDecaf(Boolean decaf) {
        isDecaf = decaf;
    }

    public Boolean getHasCream() {
        return hasCream;
    }

    public void setHasCream(Boolean hasCream) {
        this.hasCream = hasCream;
    }
}
