package com.elatier.bank;

import com.elatier.bank.core.Account;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by kriaval on 03/08/15.
 */
public class BankApplicationAcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<BankConfiguration> RULE =
            new DropwizardAppRule<BankConfiguration>(BankApplication.class, ResourceHelpers.resourceFilePath("bank-test.yml"));
    private final BigDecimal initialBalance = new BigDecimal(500.57);
    private final Account account = new Account(1, "NiceAccount", initialBalance);

    // Create the test database with the LiquiBase migrations.
    @BeforeClass
    public static void up() throws Exception {
        ManagedDataSource ds = RULE.getConfiguration().getDataSourceFactory().build(
                RULE.getEnvironment().metrics(), "migrations");
        try (Connection connection = ds.getConnection()) {
            Liquibase migrator = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
            migrator.update("");
        }
    }

    @Test
    public void listAccounts() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

        Response repsonse = client.target(
                String.format("http://localhost:%d/account/list", RULE.getLocalPort()))
                .request().accept(MediaType.APPLICATION_JSON_TYPE)
                .get();
        assertThat(repsonse.getStatus()).isEqualTo(200);

    }
}