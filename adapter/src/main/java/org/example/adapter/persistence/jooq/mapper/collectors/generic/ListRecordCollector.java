package org.example.adapter.persistence.jooq.mapper.collectors.generic;

import org.jooq.Record;
import org.jooq.TableField;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public class ListRecordCollector<E> implements Collector<Record, Map<String, E>, List<E>> {

    private final TableField<?, ?> idKey;
    private final Function<Record, E> createEntity;
    private final BiFunction<Record, E, E> updateEntity;

    public ListRecordCollector(TableField<?, ?> idKey, Function<Record, E> createEntity, BiFunction<Record, E, E> updateEntity) {
        this.idKey = idKey;
        this.createEntity = createEntity;
        this.updateEntity = updateEntity;
    }

    @Override
    public Supplier<Map<String, E>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, E>, Record> accumulator() {
        return (idToEntityMap, record) -> {
            final String key = record.get(idKey).toString();
            idToEntityMap.putIfAbsent(key, createEntity.apply(record));
            idToEntityMap.put(key, updateEntity.apply(record, idToEntityMap.get(key)));
        };
    }

    @Override
    public BinaryOperator<Map<String, E>> combiner() {
        return null;
    }

    @Override
    public Function<Map<String, E>, List<E>> finisher() {
        return idToEntityMap -> new ArrayList<>(idToEntityMap.values());
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
