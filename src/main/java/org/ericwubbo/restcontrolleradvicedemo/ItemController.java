package org.ericwubbo.restcontrolleradvicedemo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;

    @GetMapping
    public Iterable<Item> getAll() {
        return itemRepository.findAll();
    }

    @GetMapping("{id}")
    public Item getById(@PathVariable long id) {
        return itemRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<Item> create(@RequestBody Item item, UriComponentsBuilder uriComponentsBuilder) {
        if (item.getName() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0 || item.getId() != null)
            throw new BadRequestException();
        itemRepository.save(item);
        var location = uriComponentsBuilder.path("{id}").buildAndExpand(item.getId()).toUri();
        return ResponseEntity.created(location).body(item);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) throw new NotFoundException();
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public Item update(@PathVariable Long id, @RequestBody Item itemUpdates) {
        if (itemUpdates.getId() != null) throw new BadRequestException();
        Item item = itemRepository.findById(id).orElseThrow(NotFoundException::new);
        var newName = itemUpdates.getName();
        if (newName != null) { // a name has been specified
            if (newName.isBlank()) throw new BadRequestException();
            item.setName(newName);
        }
        var newPrice = itemUpdates.getPrice();
        if (newPrice != null) { // a price has been specified
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException();
            item.setPrice(newPrice);
        }
        return itemRepository.save(item);
    }
}
