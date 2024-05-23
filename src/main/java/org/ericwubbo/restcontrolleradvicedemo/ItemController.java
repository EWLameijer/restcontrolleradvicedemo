package org.ericwubbo.restcontrolleradvicedemo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;

    private final ItemService itemService;

    @GetMapping
    public Iterable<Item> getAll() {
        return itemRepository.findAll();
    }

    @GetMapping("{id}")
    public Item getById(@PathVariable long id) {
        return itemRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) throw new NotFoundException();
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Item> create(@RequestBody Item item, UriComponentsBuilder uriComponentsBuilder) {
        noIdInBodyOrThrowBadRequest(item);
        itemService.validNameOrThrowBadRequest(item);
        itemService.validPriceOrThrowBadRequest(item);
        itemRepository.save(item);
        var location = uriComponentsBuilder.path("{id}").buildAndExpand(item.getId()).toUri();
        return ResponseEntity.created(location).body(item);
    }

    @PatchMapping("{id}")
    public Item update(@PathVariable Long id, @RequestBody Item itemUpdates) {
        noIdInBodyOrThrowBadRequest(itemUpdates);
        Item item = itemRepository.findById(id).orElseThrow(NotFoundException::new);
        var newName = itemUpdates.getName();
        if (newName != null) { // a name has been specified
            itemService.validNameOrThrowBadRequest(itemUpdates);
            item.setName(newName);
        }
        var newPrice = itemUpdates.getPrice();
        if (newPrice != null) { // a price has been specified
            itemService.validPriceOrThrowBadRequest(itemUpdates);
            item.setPrice(newPrice);
        }
        return itemRepository.save(item);
    }

    private void noIdInBodyOrThrowBadRequest(Item candidateItem) throws BadRequestException {
        if (candidateItem.getId() != null)
            throw new BadRequestException("A POST or PATCH request should not have a (possibly conflicting) id in the body");
    }
}
