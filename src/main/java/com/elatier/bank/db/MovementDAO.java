package com.elatier.bank.db;

import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class MovementDAO extends AbstractDAO<Movement> {
    public MovementDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Movement> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public Movement create(@Valid Movement Movement) {
        return persist(Movement);
    }

    public List<Movement> findAll() {
        return list(namedQuery("com.elatier.bank.core.Movements.findAll"));
    }

    public long getNextTransferIdFromSeq() {
        Query query =
                currentSession().createSQLQuery("call NEXT VALUE FOR transfer_seq");
        return ((BigInteger) query.uniqueResult()).longValue();
    }

    public BigDecimal getCurrentBalance(Account a) {
        //TODO to replace with aggregate and transient balances sum for better performance
        BigDecimal currentBalance = (BigDecimal) criteria().add(Restrictions.eq("changedAccId", a.getId()))
                .setProjection(Projections.sum("amount")).uniqueResult();
        return currentBalance;
    }
}
