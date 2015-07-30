package com.elatier.bank;


import com.elatier.bank.core.Person;
import com.elatier.bank.db.PersonDAO;
import com.elatier.bank.resources.PeopleResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class BankApplication extends Application<BankConfiguration> {
    public static void main(String[] args) throws Exception {
        new BankApplication().run(args);
    }

    private final HibernateBundle<BankConfiguration> hibernateBundle =
            new HibernateBundle<BankConfiguration>(Person.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(BankConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "bank";
    }

    @Override
    public void initialize(Bootstrap<BankConfiguration> bootstrap) {
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
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new PeopleResource(dao));
    }
}
