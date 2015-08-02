package com.elatier.bank.core;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "movements")
@NamedQueries({
        @NamedQuery(
                name = "com.elatier.bank.core.Movements.findAll",
                query = "SELECT p FROM Movement p"
        )
})
public class Movement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "changed_acc_id", nullable = false)
    private long changedAccId;

    @Column(name = "linked_acc_id", nullable = false)
    private long linkedAccId;

    @Column(name = "transfer_id", nullable = false)
    private long transferId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public Movement() {
    }

    public Movement(long changedAccId, long linkedAccId, long transferId, BigDecimal amount) {
        this.changedAccId = changedAccId;
        this.linkedAccId = linkedAccId;
        this.transferId = transferId;
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movement)) return false;

        Movement movement = (Movement) o;

        if (getId() != movement.getId()) return false;
        if (getChangedAccId() != movement.getChangedAccId()) return false;
        if (getLinkedAccId() != movement.getLinkedAccId()) return false;
        if (getTransferId() != movement.getTransferId()) return false;
        return getAmount().equals(movement.getAmount());

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (int) (getChangedAccId() ^ (getChangedAccId() >>> 32));
        result = 31 * result + (int) (getLinkedAccId() ^ (getLinkedAccId() >>> 32));
        result = 31 * result + (int) (getTransferId() ^ (getTransferId() >>> 32));
        result = 31 * result + getAmount().hashCode();
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChangedAccId() {
        return changedAccId;
    }

    public void setChangedAccId(long changedAccId) {
        this.changedAccId = changedAccId;
    }

    public long getLinkedAccId() {
        return linkedAccId;
    }

    public void setLinkedAccId(long linkedAccId) {
        this.linkedAccId = linkedAccId;
    }

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

}

