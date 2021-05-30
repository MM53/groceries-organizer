package org.example.plugins.persistence.jooq.configuration;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@PropertySource("classpath:postgres-db.properties")
@Profile("production")
public class PostgresConnection implements JooqConnection {

    private DSLContext context;

    @Autowired
    public PostgresConnection(Environment env) {
        String userName = env.getProperty("jdbc.user");
        String password = env.getProperty("jdbc.password");
        String url = env.getProperty("jdbc.url");

        try {
            Class.forName("org.postgresql.Driver");
            final Connection connection = DriverManager.getConnection(url, userName, password);
            final Configuration configuration = new DefaultConfiguration().set(connection)
                                                                          .set(SQLDialect.POSTGRES)
                                                                          .set(new CustomRecordMapperProvider());
            context = DSL.using(configuration);
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DSLContext getContext() {
        return context;
    }
}
