package com.elatier.bank.core;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(
                name = "com.elatier.bank.core.Account.findAll",
                query = "SELECT a FROM Account a"
        )
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "initial_balance", nullable = false)
    private float initialBalance;

    public Account() {
    }

    public Account(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(float initialBalance) {
        this.initialBalance = initialBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account)) {
            return false;
        }

        final Account that = (Account) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
