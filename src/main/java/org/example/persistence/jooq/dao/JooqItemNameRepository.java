package org.example.persistence.jooq.dao;

import org.example.entities.Item;
import org.example.entities.ItemNameAlternative;
import org.example.persistence.jooq.configuration.JooqConnection;
import org.example.repositories.ItemNameRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.example.persistence.jooq.generated.Tables.ITEM_ALTERNATIVE_NAME;

@Component
public class JooqItemNameRepository implements ItemNameRepository {

    private final DSLContext context;

    @Autowired
    public JooqItemNameRepository(JooqConnection connection) {
        this.context = connection.getContext();
    }

    @Override
    public void save(ItemNameAlternative itemNameAlternative) {
        context.newRecord(ITEM_ALTERNATIVE_NAME, itemNameAlternative)
               .merge();
    }

    @Override
    public Optional<ItemNameAlternative> findAlternativeByName(String name) {
        return context.fetchOptional(ITEM_ALTERNATIVE_NAME, ITEM_ALTERNATIVE_NAME.NAME.eq(name))
                      .map(record -> record.into(ItemNameAlternative.class));
    }

    @Override
    public List<ItemNameAlternative> findAlternativesForItem(Item item) {
        return context.fetch(ITEM_ALTERNATIVE_NAME, ITEM_ALTERNATIVE_NAME.ALTERNATIVE_FOR.eq(item.getPrimaryName()))
                      .into(ItemNameAlternative.class);
    }

    @Override
    public List<ItemNameAlternative> getAll() {
        return context.fetch(ITEM_ALTERNATIVE_NAME)
                      .into(ItemNameAlternative.class);
    }
}
