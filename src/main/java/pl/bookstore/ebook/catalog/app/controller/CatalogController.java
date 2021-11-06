package pl.bookstore.ebook.catalog.app.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pl.bookstore.ebook.catalog.domain.model.Book;
import pl.bookstore.ebook.catalog.app.service.CatalogService;

@Controller
@RequiredArgsConstructor
public class CatalogController {
  private final CatalogService service;

  public List<Book> findByTitle(String title) {
    return service.findByTitle(title);
  }
  public List<Book> findByAuthor(String author) {
    return service.findByAuthor(author);
  }
}
