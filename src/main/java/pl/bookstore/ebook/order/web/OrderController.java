package pl.bookstore.ebook.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.bookstore.ebook.catalog.web.CreatedURI;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderCommand;
import pl.bookstore.ebook.order.app.port.ManageOrderUseCase.PlaceOrderResponse;
import pl.bookstore.ebook.order.app.port.QueryOrderUseCase;
import pl.bookstore.ebook.order.domain.OrderItem;
import pl.bookstore.ebook.order.domain.OrderStatus;
import pl.bookstore.ebook.order.domain.Recipient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
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
  public ResponseEntity<?> getById(@PathVariable Long id) {
    return queryOrder
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public ResponseEntity<?> addOrder(@RequestBody CreateOrderCommand command) {
    final PlaceOrderResponse response = manageOrder.placeOrder(command.toPlaceOrderCommand());
    if (response.isSuccess()) {
      return ResponseEntity.created(orderUri(response.getOrderId())).build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}/status")
  @ResponseStatus(ACCEPTED)
  public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
    OrderStatus orderStatus =
        OrderStatus.checkString(command.status)
            .orElseThrow(
                () ->
                    new ResponseStatusException(BAD_REQUEST, "Unknown status: " + command.status));
    manageOrder.updateOrderStatus(id, orderStatus);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void deleteOrder(@PathVariable Long id) {
    manageOrder.deleteOrderById(id);
  }

  URI orderUri(Long orderId) {
    return new CreatedURI("/" + orderId).uri();
  }

  @Data
  static class CreateOrderCommand {
    List<OrderItemCommand> items;
    RecipientCommand recipient;

    PlaceOrderCommand toPlaceOrderCommand() {
      List<OrderItem> orderItems =
          items.stream()
              .map(item -> new OrderItem(item.bookId, item.quantity))
              .collect(Collectors.toList());
      return new PlaceOrderCommand(orderItems, recipient.toRecipient());
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
