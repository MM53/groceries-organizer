package org.example.configuration;

import org.example.repositories.ItemRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("org.example")
public class TestConfig {

    @Bean
    @Primary
    public ItemRepository itemRepository() {
        return Mockito.mock(ItemRepository.class);
    }
}
