package pl.bookstore.ebook.catalog.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.bookstore.ebook.catalog.db.BookJpaRepository;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.db.OrderJpaRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
/*
Need to be done in PSQL, in case of h2-database it can't be SQLError connected
with maximal column length
*/
@SpringBootTest
@Transactional
class CatalogInitializerServiceTest {
    @Autowired CatalogInitializerService service;

    @Autowired OrderJpaRepository orderJpaRepository;

    @Autowired QueryOrderUseCase queryOrderUseCase;

    @Autowired BookJpaRepository repository;

    @Test
    public void placeOrderInitializeProperStatement() {
        // given+when
        service.load();

        // then
        assertTrue(repository.findAll().size() > 0);
        assertTrue(orderJpaRepository.findAll().size() > 0);
    }
}
