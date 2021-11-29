package pl.bookstore.ebook.order.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Recipient {
  String name;
  String phone;
  String street;
  String city;
  String zipCode;
  String email;
}
