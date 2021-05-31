package de.dhbw.plugins.persistence.jooq.configuration;

import org.jooq.DSLContext;

public interface JooqConnection {
    public DSLContext getContext();
}
