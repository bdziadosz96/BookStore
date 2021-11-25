package pl.bookstore.ebook.order.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recipient {
  String name;
  String phone;
  String street;
  String city;
  String zipCode;
  String email;
}
