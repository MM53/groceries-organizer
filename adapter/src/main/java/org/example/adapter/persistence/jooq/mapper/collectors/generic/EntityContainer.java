package org.example.adapter.persistence.jooq.mapper.collectors.generic;

import java.util.function.Function;

public class EntityContainer<E> {
    private E entity;

    public EntityContainer() {
    }

    public E get() {
        return entity;
    }

    public void set(E entity) {
        this.entity = entity;
    }

    public boolean isEmpty() {
        return entity == null;
    }

    public void update(Function<E, E> function) {
        entity = function.apply(entity);
    }
}
