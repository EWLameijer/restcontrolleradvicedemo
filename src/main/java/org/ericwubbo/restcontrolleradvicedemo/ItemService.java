package org.ericwubbo.restcontrolleradvicedemo;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ItemService {
    public void validNameOrThrowBadRequest(Item candidateItem) throws BadRequestException {
        String name = candidateItem.getName();
        if (name == null) throw new BadRequestException("An item should have a name");
        String trimmedName = name.trim();
        if (trimmedName.length() < 2)
            throw new BadRequestException("The name of an item should consist of more than one non-blank character");
    }

    public void validPriceOrThrowBadRequest(Item candidateItem) throws BadRequestException {
        BigDecimal price = candidateItem.getPrice();
        if (price == null) throw new BadRequestException("Each item should have a price");
        if (price.compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("The price assigned to an item should not be zero or negative");
    }
}
