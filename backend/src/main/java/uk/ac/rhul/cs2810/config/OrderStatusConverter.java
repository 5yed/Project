package uk.ac.rhul.cs2810.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.rhul.cs2810.model.OrderStatus;

@Converter(autoApply = false)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

  @Override
  public String convertToDatabaseColumn(OrderStatus attribute) {
    if (attribute == null) return null;
    return attribute.toDbValue();
  }

  @Override
  public OrderStatus convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    return OrderStatus.fromDbValue(dbData);
  }
}