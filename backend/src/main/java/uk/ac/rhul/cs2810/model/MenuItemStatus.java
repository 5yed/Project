package uk.ac.rhul.cs2810.model;

public enum MenuItemStatus {
  AVAILABLE,
  OUT_OF_STOCK,
  HIDDEN;

  public String toDbValue() {
    return name().toLowerCase();
  }

  public static MenuItemStatus fromDbValue(String value) {
    if (value == null) return null;
    return MenuItemStatus.valueOf(value.toUpperCase());
  }
}
