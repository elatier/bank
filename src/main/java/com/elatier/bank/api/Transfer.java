package com.elatier.bank.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.validation.ValidationMethod;

import java.math.BigDecimal;

public class Transfer {
    //TODO add validators
    private long id;
    private long sourceAccId;
    private long destAccId;
    private BigDecimal amount;

    public Transfer() {
        // Jackson deserialization
    }

    public Transfer(long id, long sourceAccId, long destAccId, BigDecimal amount) {
        this.id = id;
        this.sourceAccId = sourceAccId;
        this.destAccId = destAccId;
        this.amount = amount;
    }

    @JsonIgnore

    @ValidationMethod(message = "amount may not be fractional")
    public boolean isNotFractional() {
        //TODO FIX THIS
        return true; //!(amount.);
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
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
