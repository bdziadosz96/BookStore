package pl.bookstore.ebook.catalog.app;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.app.port.CatalogInitializerUseCase;
import pl.bookstore.ebook.catalog.app.port.CatalogUseCase;
import pl.bookstore.ebook.catalog.db.AuthorJpaRepository;
import pl.bookstore.ebook.catalog.domain.Book;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.Recipient;

@Service
@AllArgsConstructor
@Slf4j
class CatalogInitializerService implements CatalogInitializerUseCase {
    private final CatalogUseCase catalog;
    private final ManageOrderUseCase manageOrder;
    private final QueryOrderUseCase queryOrder;
    private final AuthorJpaRepository authorRepository;

    @Override
    @Transactional
    public void load() {
        initData();
        placeOrder();
    }

    private void initData() {
        ClassPathResource resource = new ClassPathResource("books.csv");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            CsvToBean<CsvBook> build = new CsvToBeanBuilder<CsvBook>(reader)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withType(CsvBook.class)
                    .build();
            build.forEach(this::initBook);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse CSV faile", e);
        }
    }

    private void initBook(CsvBook csvBook) {
        new CatalogUseCase.CreateBookCommand(
                csvBook.title,
                Set.of(),
                csvBook.year,
                csvBook.amount,
                50L
        );
    }

    private void placeOrder() {
        Book effective_java =
                catalog
                        .findOneByTitle("Effective Java")
                        .orElseThrow(() -> new IllegalStateException("Cannot find book!"));
        Book clean_code =
                catalog
                        .findOneByTitle("Java - Clean Architecture")
                        .orElseThrow(() -> new IllegalStateException("Cannot find book!"));

        Recipient recipient =
                Recipient.builder()
                        .city("Warszawa")
                        .email("bdziadosz96@icloud.com")
                        .name("Bartek Dziadosz")
                        .phone("700-600-500")
                        .street("Kwiatowa 18b/10")
                        .zipCode("00-000")
                        .build();

        ManageOrderUseCase.PlaceOrderCommand command =
                ManageOrderUseCase.PlaceOrderCommand.builder()
                        .recipient(recipient)
                        .item(new ManageOrderUseCase.OrderItemCommand(effective_java.getId(), 16))
                        .item(new ManageOrderUseCase.OrderItemCommand(clean_code.getId(), 7))
                        .build();

        ManageOrderUseCase.PlaceOrderResponse response = manageOrder.placeOrder(command);
        String result = response.handle(
                orderId -> "Created ORDER with id: " + orderId,
                error -> "Failed to created order: " + error
        );
        log.info(result);

        queryOrder.findAll()
                .forEach(order -> log.info("GET ORDER WITH TOTAL PRICE " + order.totalPrice()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvBook {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private Integer year;
        @CsvBindByName
        private BigDecimal amount;
        @CsvBindByName
        private String thumbnail;

    }
}
