package org.example.services;

import org.example.entities.aggregateRoots.Item;
import org.example.exceptions.ItemNotFoundException;
import org.example.exceptions.UnitMismatchException;
import org.example.repositories.ItemRepository;
import org.example.units.Unit;
import org.example.units.UnitType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ItemUtilService implements ApplicationContextAware {

    private ItemRepository itemRepository;

    public ItemUtilService() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        itemRepository = applicationContext.getBean(ItemRepository.class);
    }

    public void validate(String itemReference, UnitType actual) {
        UnitType expected = itemRepository.findItemById(itemReference)
                                          .map(Item::getUnitType)
                                          .orElseThrow(() -> new ItemNotFoundException(itemReference));
        if (!actual.equals(expected)) {
            throw new UnitMismatchException(expected, actual);
        }
    }

    public void validateExistence(String itemReference) {
        itemRepository.findItemById(itemReference)
                      .orElseThrow(() -> new ItemNotFoundException(itemReference));
    }

    public Unit getUnit(String itemReference) {
        return itemRepository.findItemById(itemReference)
                             .map(Item::getUnitType)
                             .map(UnitType::getBase)
                             .orElseThrow(() -> new ItemNotFoundException(itemReference));
    }
}