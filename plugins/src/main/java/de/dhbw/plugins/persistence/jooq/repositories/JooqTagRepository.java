package de.dhbw.plugins.persistence.jooq.repositories;

import de.dhbw.adapter.persistence.jooq.generated.Tables;
import de.dhbw.aggregates.Tag;
import de.dhbw.plugins.persistence.jooq.configuration.JooqConnection;
import de.dhbw.repositories.TagRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JooqTagRepository implements TagRepository {

    private final DSLContext context;

    @Autowired
    public JooqTagRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(Tag tag) {
        context.newRecord(Tables.TAG, tag).merge();
    }

    @Override
    public Optional<Tag> findTagByName(String name) {
        return context.fetchOptional(Tables.TAG, Tables.TAG.NAME.eq(name))
                      .map(tagRecord -> tagRecord.into(Tag.class));
    }

    @Override
    public List<Tag> getAll() {
        return context.fetch(Tables.TAG)
                      .map(record -> record.into(Tag.class));
    }

    @Override
    public void delete(Tag tag) {
        context.newRecord(Tables.TAG, tag).delete();
    }
}
