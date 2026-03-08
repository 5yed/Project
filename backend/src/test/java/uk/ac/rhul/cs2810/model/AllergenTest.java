package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AllergenTest {

  @Test
  void testGetters() {
    Allergen allergen = Allergen.CELERY;

    assertEquals(1, allergen.getId());
    assertEquals("celery", allergen.getName());
  }

  @Test
  void testFromIdValid() {
    Allergen result = Allergen.fromId(7);
    assertEquals(Allergen.MILK, result);
  }

  @Test
  void testFromIdInvalid() {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> Allergen.fromId(999));
    assertTrue(ex.getMessage().contains("invalid allergen ID"));
  }

  @Test
  void testFromNameValid() {
    Allergen result = Allergen.fromName("milk");
    assertEquals(Allergen.MILK, result);
  }

  @Test
  void testFromNameCaseInsensitive() {
    Allergen result = Allergen.fromName("MiLK");
    assertEquals(Allergen.MILK, result);
  }

  @Test
  void testFromNameInvalid() {
    Exception ex = assertThrows(IllegalArgumentException.class, () -> Allergen.fromName("unknown"));
    assertTrue(ex.getMessage().contains("Invalid allergen name"));
  }

  @Test
  void testNameToIdValid() {
    Integer id = Allergen.nameToId("milk");
    assertEquals(7, id);
  }

  @Test
  void testNameToIdInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Allergen.nameToId("UNKNOWN"));
  }

  @Test
  void testIdToNameValid() {
    String name = Allergen.idToName(7);
    assertEquals("milk", name);
  }

  @Test
  void testIdToNameInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Allergen.idToName(999));
  }

  @Test
  void testAllEnumValuesHaveUniqueIds() {
    var ids = new java.util.HashSet<Integer>();
    for (Allergen a : Allergen.values()) {
      assertTrue(ids.add(a.getId()), "Duplicate id: " + a.getId());
    }
  }
}
