package com.elatier.bank;


import com.elatier.bank.core.Account;
import com.elatier.bank.core.Movement;
import com.elatier.bank.db.AccountDAO;
import com.elatier.bank.db.MovementDAO;
import com.elatier.bank.resources.AccountResource;
import com.elatier.bank.resources.TransferResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class BankApplication extends Application<BankConfiguration> {
    private final HibernateBundle<BankConfiguration> hibernateBundle =
            new HibernateBundle<BankConfiguration>(Account.class, Movement.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(BankConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    public static void main(String[] args) throws Exception {
        new BankApplication().run(args);
    }

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public void initialize(Bootstrap<BankConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<BankConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(BankConfiguration configuration) {
                return configuration.getSwaggerBundleConfiguration();
            }
        });
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );
        bootstrap.addBundle(new MigrationsBundle<BankConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(BankConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(BankConfiguration configuration, Environment environment) {
        final AccountDAO aDAO = new AccountDAO(hibernateBundle.getSessionFactory());
        final MovementDAO mDAO = new MovementDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new AccountResource(aDAO, mDAO));
        environment.jersey().register(new TransferResource(aDAO, mDAO));
    }
}
