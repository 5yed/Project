package uk.ac.rhul.cs2810.dto;

import uk.ac.rhul.cs2810.model.MenuItemStatus;

public class UpdateMenuItemRequest {

  private String name;
  private String description;
  private String image;

  private Double kcal;
  private Double price;
  private Double fat;
  private Double protein;
  private Double carbs;

  private MenuItemStatus status;

  private Long categoryId;

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }

  public Double getKcal() {
    return kcal;
  }

  public Double getPrice() {
    return price;
  }

  public Double getFat() {
    return fat;
  }

  public Double getProtein() {
    return protein;
  }

  public Double getCarbs() {
    return carbs;
  }

  public MenuItemStatus getStatus() {
    return status;
  }

  public Long getCategoryId() {
    return categoryId;
  }
}
