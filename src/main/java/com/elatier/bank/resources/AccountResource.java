package com.elatier.bank.resources;

import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.elatier.bank.exceptions.InvalidRequestException;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.*;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;


@Path("/account/")
@Api(value = "/account/", description = "Operations about accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    private final AccountDAO accountDAO;
    private final MovementDAO movementDAO;

    public AccountResource(AccountDAO accountDAO, MovementDAO mDAO) {
        this.accountDAO = accountDAO;
        this.movementDAO = mDAO;
    }

    @GET
    @ApiOperation(value = "Find account by ID", notes = "Find account by ID", response = Account.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Account not found")
    })
    @Path("/{accId}/")
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

    @ApiOperation(value = "Find account's current balance", notes = "Find account's current balance", response = BigDecimal.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Account not found")
    })
    @Path("/{accId}/currentBalance")
    @GET
    @UnitOfWork
    public BigDecimal getCurrentBalance(@PathParam("accId") LongParam accId) {
        Account a = findSafely(accId.get());
        return movementDAO.getCurrentBalance(a);
    }

    @POST
    @ApiOperation(value = "Create new account", notes = "Create new account with starting balance", response = Account.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid parameters supplied")
    })
    @UnitOfWork
    public Account createAccount(@ApiParam @Valid Account account) {
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
    @Path("/list")
    @ApiOperation(value = "List all accounts", notes = "List all accounts", response = Account.class)
    @UnitOfWork
    public List<Account> listAccounts() {
        return accountDAO.findAll();
    }
}
