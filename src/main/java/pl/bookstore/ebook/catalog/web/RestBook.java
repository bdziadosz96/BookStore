package pl.bookstore.ebook.catalog.web;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Value;
import pl.bookstore.ebook.catalog.domain.RestAuthor;

@Value
class RestBook {
    Long id;
    String title;
    Integer year;
    BigDecimal price;
    String coverUrl;
    Long available;
    Set<RestAuthor> authors;
}
