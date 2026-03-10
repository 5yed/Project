package uk.ac.rhul.cs2810.model;

public enum Allergen {
  CELERY(1, "celery"),
  GLUTEN(2, "gluten"),
  CRUSTACEANS(3, "crustaceans"),
  EGGS(4, "eggs"),
  FISH(5, "fish"),
  LUPIN(6, "lupin"),
  MILK(7, "milk"),
  MOLLUSCS(8, "molluscs"),
  MUSTARD(9, "mustard"),
  NUTS(10, "nuts"),
  PEANUTS(11, "peanuts"),
  SESAME_SEEDS(12, "sesame seeds"),
  SOYA(13, "soya"),
  SULPHUR_DIOXIDE(14, "sulphur dioxide");

  private final Integer id;
  private final String name;

  Allergen(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public static Allergen fromId(Integer id) {
    for (Allergen a : values()) {
      if (a.id.equals(id)) {
        return a;
      }
    }
    throw new IllegalArgumentException("invalid allergen ID: " + id);
  }

  public static Allergen fromName(String name) {
    for (Allergen a : values()) {
      if (a.name.equalsIgnoreCase(name)) {
        return a;
      }
    }
    throw new IllegalArgumentException("Invalid allergen name: " + name);
  }

  public static Integer nameToId(String name) {
    for (Allergen a : values()) {
      if (a.name.equals(name)) {
        return a.id;
      }
    }
    throw new IllegalArgumentException("Invalid allergen name: " + name);
  }

  public static String idToName(int id) {
    for (Allergen a : values()) {
      if (a.id.equals(id)) {
        return a.name;
      }
    }
    throw new IllegalArgumentException("Invalid allergen id: " + id);
  }
}
