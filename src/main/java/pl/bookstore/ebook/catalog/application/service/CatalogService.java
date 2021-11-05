package pl.bookstore.ebook.catalog.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.domain.repository.CatalogRepository;

import java.util.List;

@Service
public class CatalogService {
  private final CatalogRepository catalogRepository;

  public CatalogService(@Qualifier("schoolCatalogRepository") CatalogRepository catalogRepository) {
    this.catalogRepository = catalogRepository;
  }

  public List<Book> findByTitle(String title) {
    return catalogRepository.findAll().stream()
        .filter(book -> book.getTitle().startsWith(title))
        .toList();
  }
}
