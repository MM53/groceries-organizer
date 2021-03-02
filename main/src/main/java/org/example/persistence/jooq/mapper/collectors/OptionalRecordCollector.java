package org.example.persistence.jooq.mapper.collectors;

import org.jooq.Record;

import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class OptionalRecordCollector<E> implements Collector<Record, EntityContainer<E>, Optional<E>> {

    private final Function<Record, E> createEntity;
    private final BiFunction<Record, E, E> updateEntity;

    public OptionalRecordCollector(Function<Record, E> createEntity, BiFunction<Record, E, E> updateEntity) {
        this.createEntity = createEntity;
        this.updateEntity = updateEntity;
    }

    @Override
    public Supplier<EntityContainer<E>> supplier() {
        return EntityContainer::new;
    }

    @Override
    public BiConsumer<EntityContainer<E>, Record> accumulator() {
        return (entity, record) -> {
            if (entity.isEmpty()) {
                entity.set(createEntity.apply(record));
            }
            entity.update(e -> updateEntity.apply(record, e));
        };
    }

    @Override
    public BinaryOperator<EntityContainer<E>> combiner() {
        return null;
    }

    @Override
    public Function<EntityContainer<E>, Optional<E>> finisher() {
        return entity -> Optional.ofNullable(entity.get());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
