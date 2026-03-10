package uk.ac.rhul.cs2810.model;

import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Creates DietaryRestriction entity.
 */

@Entity
@Table(name = "dietary_restriction")
public class DietaryRestriction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  protected DietaryRestriction() {}

  public DietaryRestriction(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    DietaryRestriction dietaryRestriction = (DietaryRestriction) obj;
    return (name != null && name.equals(dietaryRestriction.name));
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
