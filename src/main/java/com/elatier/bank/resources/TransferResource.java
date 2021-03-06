package com.elatier.bank.resources;

import com.elatier.bank.api.Transfer;
import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.elatier.bank.exceptions.InvalidRequestException;
import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.*;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;


@Path("/transfer/")
@Api(value = "/transfer/", description = "Operations for transfers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferResource {

    private final MovementDAO movementDAO;
    private final AccountDAO accountDAO;

    public TransferResource(AccountDAO aDAO, MovementDAO movementDAO) {
        this.movementDAO = movementDAO;
        this.accountDAO = aDAO;
    }

    private Account findSafely(long accId, String message) {
        final Optional<Account> account = accountDAO.findById(accId);
        if (!account.isPresent()) {
            throw new InvalidRequestException(400, message);
        }
        return account.get();
    }

    @POST
    @ApiOperation(value = "Create new transfer", notes = "Create new transfer", response = Transfer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "May not transfer non-positive amount"),
            @ApiResponse(code = 400, message = "Source account does not have enough funds for transaction"),
            @ApiResponse(code = 400, message = "Invalid parameters supplied"),
            @ApiResponse(code = 409, message = "Source and destination accounts are the same")
    })
    @UnitOfWork
    public Transfer createTransfer(@ApiParam Transfer t) {
        //checking if amount is not null
        if (t.getAmount() == null) throw new InvalidRequestException(400, "Amount not specifiedFixin");

        //check account ids are different
        if (t.getDestAccId() == t.getSourceAccId()) {
            throw new InvalidRequestException(409, "Source and destination accounts are the same");
        }
        //check if amount is positive
        if (t.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException(400, "May not transfer non-positive amount");
        }

        //check if both accounts exist
        Account sourceAcc = findSafely(t.getSourceAccId(), "No such sourceAccId.");
        findSafely(t.getDestAccId(), "No such destAccId.");

        //check if source balance is positive
        if ((movementDAO.getCurrentBalance(sourceAcc).subtract(t.getAmount())).compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequestException(400, "Source account does not have enough funds for transaction");
        }

        long transferId = movementDAO.getNextTransferIdFromSeq();

        Movement fromMov = new Movement();
        fromMov.setChangedAccId(t.getSourceAccId());
        fromMov.setLinkedAccId(t.getDestAccId());
        fromMov.setAmount(t.getAmount().negate());
        fromMov.setTransferId(transferId);
        movementDAO.create(fromMov);

        Movement toMov = new Movement();
        toMov.setChangedAccId(t.getDestAccId());
        toMov.setLinkedAccId(t.getSourceAccId());
        toMov.setAmount(t.getAmount());
        toMov.setTransferId(transferId);
        movementDAO.create(toMov);

        t.setId(transferId);
        return t;
    }

    @GET
    @Path("/list")
    @ApiOperation(value = "List all movements (for debug)", notes = "List all movements (for debug)", response = Movement.class)
    @UnitOfWork
    public List<Movement> listMovements() {
        return movementDAO.findAll();
    }
}
