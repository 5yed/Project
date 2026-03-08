package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class DietaryRestrictionTest {

  @Test
  void testConstructorAndGetter() {
    DietaryRestriction dr = new DietaryRestriction("Vegan");
    assertEquals("Vegan", dr.getName());
  }

  @Test
  void testSettersAndGetters() {
    DietaryRestriction dr = new DietaryRestriction("Vegetarian");

    dr.setId(5L);
    dr.setName("Halal");

    assertEquals(5L, dr.getId());
    assertEquals("Halal", dr.getName());
  }

  @Test
  void testEqualsSameObject() {
    DietaryRestriction dr = new DietaryRestriction("Vegan");
    assertEquals(dr, dr);
  }

  @Test
  void testEqualsSameName() {
    DietaryRestriction dr1 = new DietaryRestriction("Vegan");
    DietaryRestriction dr2 = new DietaryRestriction("Vegan");

    assertEquals(dr1, dr2);
  }

  @Test
  void testEqualsDifferentName() {
    DietaryRestriction dr1 = new DietaryRestriction("Vegan");
    DietaryRestriction dr2 = new DietaryRestriction("Halal");

    assertNotEquals(dr1, dr2);
  }

  @Test
  void testEqualsNull() {
    DietaryRestriction dr = new DietaryRestriction("Vegan");
    assertNotEquals(null, dr);
  }

  @Test
  void testEqualsDifferentClass() {
    DietaryRestriction dr = new DietaryRestriction("Vegan");
    assertNotEquals("Vegan", dr);
  }

  @Test
  void testHashCodeSameForEqualObjects() {
    DietaryRestriction dr1 = new DietaryRestriction("Vegan");
    DietaryRestriction dr2 = new DietaryRestriction("Vegan");

    assertEquals(dr1.hashCode(), dr2.hashCode());
  }
}

