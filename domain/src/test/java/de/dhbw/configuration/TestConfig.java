package de.dhbw.configuration;

import de.dhbw.repositories.ItemRepository;
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
}
