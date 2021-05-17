package org.example.plugins.jooq.repositories;

import org.example.entities.aggregateRoots.Tag;
import org.example.plugins.jooq.configuration.JooqConnection;
import org.example.repositories.TagRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.example.adapter.persistence.jooq.generated.Tables.TAG;

@Component
public class JooqTagRepository implements TagRepository {

    private final DSLContext context;

    @Autowired
    public JooqTagRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Tag tag) {
        context.newRecord(TAG, tag).merge();
    }

    @Override
    public Optional<Tag> findTagByName(String name) {
        return context.fetchOptional(TAG, TAG.NAME.eq(name))
                      .map(tagRecord -> tagRecord.into(Tag.class));
    }

    @Override
    public List<Tag> getAll() {
        return context.fetch(TAG)
                      .map(record -> record.into(Tag.class));
    }

    @Override
    public void delete(Tag tag) {
        context.newRecord(TAG, tag).delete();
    }
}
