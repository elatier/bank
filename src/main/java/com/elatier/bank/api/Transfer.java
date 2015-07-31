package com.elatier.bank.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transfer {
    //TODO add validators
    private long id;
    private long sourceAccId;
    private long destAccId;
    private float amount;

    public Transfer() {
        // Jackson deserialization
    }

    public Transfer(long id, long sourceAccId, long destAccId, float amount) {
        this.id = id;
        this.sourceAccId = sourceAccId;
        this.destAccId = destAccId;
        this.amount = amount;
    }

    @JsonProperty
    public long getSourceAccId() {
        return sourceAccId;
    }

    @JsonProperty
    public long getDestAccId() {
        return destAccId;
    }

    @JsonProperty
    public float getAmount() {
        return amount;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", sourceAccId=" + sourceAccId +
                ", destAccId=" + destAccId +
                ", amount=" + amount +
                '}';
    }
}
