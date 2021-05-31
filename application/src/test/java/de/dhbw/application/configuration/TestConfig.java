package de.dhbw.application.configuration;

import de.dhbw.repositories.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("de.dhbw")
public class TestConfig {

    @Bean
    @Primary
    public ItemRepository itemRepository() {
        return Mockito.mock(ItemRepository.class);
    }

    @Bean
    @Primary
    public RecipeRepository recipeRepository() {
        return Mockito.mock(RecipeRepository.class);
    }

    @Bean
    @Primary
    public StoredItemRepository storedItemRepository() {
        return Mockito.mock(StoredItemRepository.class);
    }

    @Bean
    @Primary
    public ShoppingListRepository shoppingListRepository() {
        return Mockito.mock(ShoppingListRepository.class);
    }

    @Bean
    @Primary
    public TagRepository tagRepository() {
        return Mockito.mock(TagRepository.class);
    }
}
