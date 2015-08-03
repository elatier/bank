package com.elatier.bank.resources;

import com.elatier.bank.core.Account;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.google.common.base.Optional;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by kriaval on 03/08/15.
 */
public class AccountResourceTest {

    private static final AccountDAO aDAO = mock(AccountDAO.class);
    private static final MovementDAO mDAO = mock(MovementDAO.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountResource(aDAO, mDAO))
            .build();

    private final BigDecimal initialBalance = new BigDecimal(500.57);
    private final Account account = new Account(1, "NiceAccount", initialBalance);
    private final Optional<Account> acc = Optional.of(account);

    @Before
    public void setUp() throws Exception {
        //clear mocks before each test
        reset(mDAO, aDAO);
    }

    @Test
    public void testGetAccount() throws Exception {
        when(aDAO.findById(account.getId())).thenReturn(acc);

        assertThat(resources.client().target("/account/1/").request().get(Account.class))
                .isEqualTo(account);
        verify(aDAO).findById(account.getId());
    }

    @Test
    public void testGetCurrentBalance() throws Exception {
        BigDecimal balance = new BigDecimal(456.45);
        when(aDAO.findById(account.getId())).thenReturn(acc);
        when(mDAO.getCurrentBalance(account)).thenReturn(balance);

        assertThat(resources.client().target("/account/1/currentBalance").request().get(BigDecimal.class))
                .isEqualTo(balance);
        verify(mDAO).getCurrentBalance(account);
    }

}