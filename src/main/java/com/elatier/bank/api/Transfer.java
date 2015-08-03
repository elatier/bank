package com.elatier.bank.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.validation.ValidationMethod;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Transfer {
    private long id;
    @NotNull
    private long sourceAccId;
    @NotNull
    private long destAccId;
    @NotNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transfer)) return false;

        Transfer transfer = (Transfer) o;

        if (getId() != transfer.getId()) return false;
        if (getSourceAccId() != transfer.getSourceAccId()) return false;
        if (getDestAccId() != transfer.getDestAccId()) return false;
        return getAmount().equals(transfer.getAmount());

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (int) (getSourceAccId() ^ (getSourceAccId() >>> 32));
        result = 31 * result + (int) (getDestAccId() ^ (getDestAccId() >>> 32));
        result = 31 * result + getAmount().hashCode();
        return result;
    }

    @JsonIgnore

    @ValidationMethod(message = "amount scale should be no more than 2")
    public boolean isAmountCorrectScale() {
        return !(amount.stripTrailingZeros().scale() > 2);
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
