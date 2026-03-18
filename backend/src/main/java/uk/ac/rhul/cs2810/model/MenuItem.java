package uk.ac.rhul.cs2810.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.rhul.cs2810.config.MenuItemStatusConverter;

@Entity
@Table(name = "menu_item")
public class MenuItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  @Column
  private String image;

  @Column(nullable = false)
  private Double kcal = 0.0;

  @Column
  private Double fat;

  @Column
  private Double protein;

  @Column
  private Double carbs;

  @Column(nullable = false)
  @Convert(converter = MenuItemStatusConverter.class)
  private MenuItemStatus status = MenuItemStatus.AVAILABLE;

  @ManyToMany
  @JoinTable(name = "menu_item_dietary_restriction",
      joinColumns = @JoinColumn(name = "menu_item_id"),
      inverseJoinColumns = @JoinColumn(name = "dietary_restriction_id"))
  private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();

  @ManyToOne(optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private MenuItemCategory category;

  public MenuItem(String name, BigDecimal price, Double kcal, MenuItemStatus status,
      MenuItemCategory category) {
    this.name = name;
    this.price = price;
    this.kcal = kcal;
    this.status = status;
    this.category = category;
  }


  public MenuItem() {}

  @ElementCollection
  @CollectionTable(name = "menu_item_allergens", joinColumns = @JoinColumn(name = "menu_item_id"))
  @Column(name = "allergen_id")
  private List<Integer> allergens = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getImage() {
    return image;
  }

  public Double getKcal() {
    return kcal;
  }

  public List<Integer> getAllergens() {
    return allergens;
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

  public List<DietaryRestriction> getDietaryRestrictions() {
    return dietaryRestrictions;
  }

  public MenuItemCategory getCategory() {
    return category;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setKcal(Double kcal) {
    this.kcal = kcal;
  }

  public void setFat(Double fat) {
    this.fat = fat;
  }

  public void setProtein(Double protein) {
    this.protein = protein;
  }

  public void setCarbs(Double carbs) {
    this.carbs = carbs;
  }

  public void setStatus(MenuItemStatus status) {
    this.status = status;
  }

  public void addDietaryRestriction(DietaryRestriction dietaryRestriction) {
    if (!(this.dietaryRestrictions.contains(dietaryRestriction))) {
      this.dietaryRestrictions.add(dietaryRestriction);
    }
  }

  public void addAllergen(Integer allergen) {
    this.allergens.add(allergen);
  }

  public void setCategory(MenuItemCategory category) {
    this.category = category;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof MenuItem)) {
      return false;
    }

    MenuItem other = (MenuItem) o;

    // If both IDs exist, compare by ID
    if (this.id != null && other.id != null) {
      return this.id.equals(other.id);
    }

    // Otherwise compare business fields
    return java.util.Objects.equals(name, other.name)
        && java.util.Objects.equals(description, other.description)
        && java.util.Objects.equals(price, other.price)
        && java.util.Objects.equals(image, other.image)
        && java.util.Objects.equals(kcal, other.kcal) && java.util.Objects.equals(fat, other.fat)
        && java.util.Objects.equals(protein, other.protein)
        && java.util.Objects.equals(carbs, other.carbs) && status == other.status
        && java.util.Objects.equals(category, other.category)
        && java.util.Objects.equals(dietaryRestrictions, other.dietaryRestrictions)
        && java.util.Objects.equals(allergens, other.allergens);
  }

  @Override
  public int hashCode() {
    if (id != null) {
      return id.hashCode();
    }

    return java.util.Objects.hash(name, description, price, image, kcal, fat, protein, carbs,
        status, category, dietaryRestrictions, allergens);
  }
}
