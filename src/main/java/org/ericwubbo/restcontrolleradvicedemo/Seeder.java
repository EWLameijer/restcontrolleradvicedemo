package org.ericwubbo.restcontrolleradvicedemo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        if (itemRepository.count() == 0) {
            itemRepository.saveAll(List.of(
                    new Item("chocolate bar", "2.20"),
                    new Item("syrup waffles", "1.96")));
        }
    }
}
