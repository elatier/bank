package com.elatier.bank.resources;

import com.elatier.bank.api.Transfer;
import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.google.common.base.Optional;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by kriaval on 03/08/15.
 */
public class TransferResourceTest {


    private static final AccountDAO aDAO = mock(AccountDAO.class);
    private static final MovementDAO mDAO = mock(MovementDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TransferResource(aDAO, mDAO))
            .build();

    private final BigDecimal initialBalance = new BigDecimal(500.57);
    private final Account accountFrom = new Account(1, "SourceAccount", initialBalance);
    private final Account accountTo = new Account(2, "DestAccount", initialBalance);
    private final Optional<Account> accFrom = Optional.of(accountFrom);
    private final Optional<Account> accTo = Optional.of(accountTo);

    @Before
    public void setUp() throws Exception {
        reset(mDAO, aDAO);

    }

    @Test
    public void testCreateTransfer() {
        BigDecimal balance = new BigDecimal(456.45);
        BigDecimal transferAmount = new BigDecimal(40.45);
        long transferId = 656l;

        when(aDAO.findById(accountFrom.getId())).thenReturn(accFrom);
        when(aDAO.findById(accountTo.getId())).thenReturn(accTo);

        when(mDAO.getCurrentBalance(accountFrom)).thenReturn(balance);
        when(mDAO.getNextTransferIdFromSeq()).thenReturn(transferId);

        //create input
        Transfer transferInput = new Transfer(0, accountFrom.getId(), accountTo.getId(), transferAmount);
        //create output
        Transfer transferOutput = new Transfer(transferId, accountFrom.getId(), accountTo.getId(), transferAmount);

        //create expected output
        Movement fromMov = new Movement();
        fromMov.setChangedAccId(transferInput.getSourceAccId());
        fromMov.setLinkedAccId(transferInput.getDestAccId());
        fromMov.setAmount(transferInput.getAmount().negate());
        fromMov.setTransferId(transferId);

        Movement toMov = new Movement();
        toMov.setChangedAccId(transferInput.getDestAccId());
        toMov.setLinkedAccId(transferInput.getSourceAccId());
        toMov.setAmount(transferInput.getAmount());
        toMov.setTransferId(transferId);

        when(mDAO.create(fromMov)).thenReturn(fromMov);
        when(mDAO.create(toMov)).thenReturn(toMov);

        Entity<Transfer> transferEntity = Entity.entity(transferInput, MediaType.APPLICATION_JSON_TYPE);

        Response r = resources.client().target("/transfer/").request().post(transferEntity);
        assertThat(r.readEntity(Transfer.class)).isEqualTo(transferOutput);

        verify(aDAO).findById(accountFrom.getId());
        verify(aDAO).findById(accountFrom.getId());
        verify(mDAO).getCurrentBalance(accountFrom);
        verify(mDAO).getNextTransferIdFromSeq();
        verify(mDAO).create(fromMov);
        verify(mDAO).create(toMov);

    }


}