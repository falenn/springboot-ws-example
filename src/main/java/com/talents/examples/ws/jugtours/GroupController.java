package com.talents.examples.ws.jugtours;

import com.talents.examples.ws.jugtours.model.Group;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GroupController {

  private final Logger log = LoggerFactory.getLogger(GroupController.class);

  /**
   * example request:  http://localhost:9999/api/groups
   * @return {{@link Collection}} {{@link Group}}
   */
  @GetMapping("/groups")
  Collection<Group> groups() {
    Collection<Group> groups = new ArrayList<Group>();
    Group g = new Group();
    g.setId("1");
    g.setName("testgroup");
    g.setAddress("123 Some Street");
    g.setCity("Springfield");
    g.setStateOrProvince("VA");
    g.setCountry("USA");
    g.setPostalCode("12345");
    ((ArrayList<Group>) groups).add(g);
    return groups;
  }
}
