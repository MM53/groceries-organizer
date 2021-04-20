package org.example.persistence.jooq.mapper.collectors;

import org.example.persistence.jooq.mapper.collectors.generic.ListRecordCollector;
import org.example.persistence.jooq.mapper.collectors.generic.OptionalRecordCollector;
import org.jooq.Record;
import org.jooq.TableField;

public abstract class RecordCollector<E> {

    public ListRecordCollector<E> toList(TableField<?, ?> idKey) {
        return new ListRecordCollector<>(idKey, this::newEntityFromRecord, this::updateEntityFromRecord);
    };

    public OptionalRecordCollector<E> toOptional() {
        return new OptionalRecordCollector<>(this::newEntityFromRecord, this::updateEntityFromRecord);
    };

    abstract E newEntityFromRecord(Record record);
    abstract E updateEntityFromRecord(Record record, E entity);
}
