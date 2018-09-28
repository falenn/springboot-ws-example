package com.talents.examples.ws.jugtours.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * https://spring.io/guides/gs/accessing-data-mongodb/
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

  @Id
  private String id;

  @NotNull
  private String name;

  private String address;
  private String city;
  private String stateOrProvince;
  private String country;
  private String postalCode;

  /*private Group(String id, String name, String address, String city, String stateOrProvince, String country, String postalCode) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.city = city;
    this.stateOrProvince = stateOrProvince;
    this.country = country;
    this.postalCode = postalCode;
  }*/

  /*public static Group create(String id, String name, String address, String city, String stateOrProvince, String country, String postalCode) {
    return new Group(id, name, address,
        city, stateOrProvince, country, postalCode);
  }*/

}
