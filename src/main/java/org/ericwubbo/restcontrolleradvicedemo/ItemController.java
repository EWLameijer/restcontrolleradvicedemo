package org.ericwubbo.restcontrolleradvicedemo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

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

    private ResponseEntity<Void> returnBadRequestIfIdIsPresent(Long id) {
        if (id != null) return
                ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        "A PATCH request should not have a (possibly conflicting) id in the body")).build();
        return null;
    }

    @PatchMapping("{id}")
    public ResponseEntity<Item> update(@PathVariable Long id, @RequestBody Item itemUpdates) {
        returnBadRequestIfIdIsPresent(itemUpdates.getId());
        Item item = itemRepository.findById(id).orElseThrow(NotFoundException::new);
        var newName = itemUpdates.getName();
        if (newName != null) { // a name has been specified
            if (newName.isBlank()) return
                    ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                            "The name assigned to an item should not be blank")).build();
            item.setName(newName);
        }
        var newPrice = itemUpdates.getPrice();
        if (newPrice != null) { // a price has been specified
            if (newPrice.compareTo(BigDecimal.ZERO) <= 0) return
                    ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                            "The price assigned to an item should not be zero or negative")).build();
            item.setPrice(newPrice);
        }
        return ResponseEntity.ok(itemRepository.save(item));
    }
}
