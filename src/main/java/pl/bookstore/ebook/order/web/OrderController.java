package pl.bookstore.ebook.order.web;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.bookstore.ebook.catalog.web.CreatedURI;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.UpdateOrderStatusCommand;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static pl.bookstore.ebook.order.app.port.QueryOrderUseCase.OrderDto;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
class OrderController {
  private final QueryOrderUseCase queryOrder;
  private final ManageOrderUseCase manageOrder;

  @GetMapping
  @ResponseStatus(OK)
  public List<OrderDto> getAll() {
    return queryOrder.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable final Long id) {
    return queryOrder
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public ResponseEntity<?> createOrder(@RequestBody final CreateOrderCommand command) {
    return manageOrder
        .placeOrder(command.toPlaceOrderCommand())
        .handle(
            orderId -> ResponseEntity.created(orderUri(orderId)).build(),
            error -> ResponseEntity.badRequest().body(error));
  }

  @PutMapping("/{id}/status")
  @ResponseStatus(ACCEPTED)
  public ResponseEntity<?> updateOrderStatus(
      @PathVariable final Long id, @RequestBody final CreateUpdateStatusCommand command) {
    OrderStatus.checkString(command.getOrderStatus().toString());
    return manageOrder
        .updateOrderStatus(command.toUpdateOrderStatusCommand(id))
        .handle(
            statusId -> ResponseEntity.created(orderUri(id)).build(),
            error -> ResponseEntity.badRequest().body(error));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void deleteOrder(@PathVariable final Long id) {
    manageOrder.deleteOrderById(id);
  }

  private URI orderUri(final Long orderId) {
    return new CreatedURI("/" + orderId).uri();
  }

  @Data
  static class CreateOrderCommand {
    List<OrderItemCommand> items;
    RecipientCommand recipient;

    PlaceOrderCommand toPlaceOrderCommand() {
      final List<OrderItem> orderItems =
          items.stream()
              .map(item -> new OrderItem(item.bookId, item.quantity))
              .collect(Collectors.toList());
      return new PlaceOrderCommand(orderItems, recipient.toRecipient());
    }
  }

  @Data
  static class CreateUpdateStatusCommand {
    OrderStatus orderStatus;

    UpdateOrderStatusCommand toUpdateOrderStatusCommand(final Long id) {
      return new UpdateOrderStatusCommand(id, orderStatus);
    }
  }

  @Data
  static class OrderItemCommand {
    Long bookId;
    int quantity;
  }

  @Data
  static class RecipientCommand {
    String name;
    String phone;
    String street;
    String city;
    String zipCode;
    String email;

    Recipient toRecipient() {
      return new Recipient(name, phone, street, city, zipCode, email);
    }
  }

  @Data
  static class UpdateStatusCommand {
    String status;
  }
}
