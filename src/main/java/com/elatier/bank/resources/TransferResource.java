package com.elatier.bank.resources;

import com.elatier.bank.api.Transfer;
import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.elatier.bank.exceptions.InvalidRequestException;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/transfer/")
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
    @UnitOfWork
    public Transfer createTransfer(Transfer t) {
        //check account ids are different
        if (t.getDestAccId() == t.getSourceAccId()) {
            throw new InvalidRequestException(409, "Source and destination accounts are the same");
        }
        //check if amount is positive
        if (t.getAmount() <= 0) {
            throw new InvalidRequestException(400, "Can't transfer non-positive amount");
        }

        //check if both accounts exist
        Account sourceAcc = findSafely(t.getSourceAccId(), "No such sourceAccId.");
        findSafely(t.getDestAccId(), "No such destAccId.");

        //check if source balance is positive
        if (movementDAO.getCurrentBalance(sourceAcc) - t.getAmount() < 0) {
            throw new InvalidRequestException(400, "Source account does not have enough funds");
        }

        long transferId = movementDAO.getNextTransferIdFromSeq();

        Movement fromMov = new Movement();
        fromMov.setChangedAccId(t.getSourceAccId());
        fromMov.setLinkedAccId(t.getDestAccId());
        fromMov.setAmount(-1 * t.getAmount());
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
    @UnitOfWork
    public List<Movement> listMovements() {
        return movementDAO.findAll();
    }
}
