package uk.ac.rhul.cs2810.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.rhul.cs2810.model.MenuItemStatus;

@Converter(autoApply = true)
public class MenuItemStatusConverter implements AttributeConverter<MenuItemStatus, String> {

  @Override
  public String convertToDatabaseColumn(MenuItemStatus status) {
    return status.name().toLowerCase();
  }

  @Override
  public MenuItemStatus convertToEntityAttribute(String dbData) {
    return MenuItemStatus.valueOf(dbData.toUpperCase());
  }
}
