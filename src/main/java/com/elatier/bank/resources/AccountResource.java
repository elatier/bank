package com.elatier.bank.resources;

import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.elatier.bank.exceptions.InvalidRequestException;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/account/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountDAO accountDAO;
    private final MovementDAO movementDAO;

    public AccountResource(AccountDAO accountDAO, MovementDAO mDAO) {
        this.accountDAO = accountDAO;
        this.movementDAO = mDAO;
    }

    @Path("/{accId}/")
    @GET
    @UnitOfWork
    public Account getAccount(@PathParam("accId") LongParam accId) {
        return findSafely(accId.get());
    }

    private Account findSafely(long accId) {
        final Optional<Account> account = accountDAO.findById(accId);
        if (!account.isPresent()) {
            throw new InvalidRequestException(404, "No such account.");
        }
        return account.get();
    }

    @Path("/{accId}/currentBalance")
    @GET
    @UnitOfWork
    public double getCurrentBalance(@PathParam("accId") LongParam accId) {
        Account a = findSafely(accId.get());
        return movementDAO.getCurrentBalance(a);
    }

    @POST
    @UnitOfWork
    public Account createAccount(Account account) {
        accountDAO.create(account);

        //create initial balance movement
        Movement toMov = new Movement();
        toMov.setChangedAccId(account.getId());
        //dummy account
        toMov.setLinkedAccId(0);
        toMov.setAmount(account.getInitialBalance());
        toMov.setTransferId(movementDAO.getNextTransferIdFromSeq());
        movementDAO.create(toMov);
        return account;
    }

    @GET
    @UnitOfWork
    public List<Account> listAccounts() {
        return accountDAO.findAll();
    }
}
