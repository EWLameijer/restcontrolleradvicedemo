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
            return ResponseEntity.badRequest().build();
        itemRepository.save(item);
        var location = uriComponentsBuilder.path("{id}").buildAndExpand(item.getId()).toUri();
        return ResponseEntity.created(location).body(item);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) return ResponseEntity.notFound().build();
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Item> update(@PathVariable Long id, @RequestBody Item itemUpdates) {
        if (itemUpdates.getId() != null) return ResponseEntity.badRequest().build();
        Optional<Item> possibleItem = itemRepository.findById(id);
        if (possibleItem.isEmpty()) return ResponseEntity.notFound().build();
        Item item = possibleItem.get();
        var newName = itemUpdates.getName();
        if (newName != null) { // a name has been specified
            if (newName.isBlank()) return ResponseEntity.badRequest().build();
            item.setName(newName);
        }
        var newPrice = itemUpdates.getPrice();
        if (newPrice != null) { // a price has been specified
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) return ResponseEntity.badRequest().build();
            item.setPrice(newPrice);
        }
        itemRepository.save(item);
        return ResponseEntity.ok(item);
    }
}
