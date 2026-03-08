package uk.ac.rhul.cs2810.model;

public enum OrderStatus {
  CREATING,
  PLACED,
  CONFIRMED,
  IN_PROGRESS,
  READY,
  DELIVERED,
  CANCELLED;

    public String toDbValue() {
    return this.name().toLowerCase();
    }

    public static OrderStatus fromDbValue(String dbValue) {
    return OrderStatus.valueOf(dbValue.toUpperCase());
    }
}
