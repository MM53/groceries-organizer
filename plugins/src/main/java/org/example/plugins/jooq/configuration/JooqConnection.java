package org.example.plugins.jooq.configuration;

import org.jooq.DSLContext;

public interface JooqConnection {
    public DSLContext getContext();
}
