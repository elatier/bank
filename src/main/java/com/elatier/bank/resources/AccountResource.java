package com.elatier.bank.resources;

import com.elatier.bank.core.Account;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
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
            throw new NotFoundException("No such account.");
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
        return accountDAO.create(account);
    }

    @GET
    @UnitOfWork
    public List<Account> listAccounts() {
        return accountDAO.findAll();
    }
}
