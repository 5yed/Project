package uk.ac.rhul.cs2810.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.rhul.cs2810.dto.UpdateMenuItemRequest;
import uk.ac.rhul.cs2810.model.Allergen;
import uk.ac.rhul.cs2810.model.DietaryRestriction;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.MenuItemCategory;
import uk.ac.rhul.cs2810.repository.MenuItemCategoryRepository;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;

@Service
public class MenuItemService {

  private final MenuItemRepository menuItemRepository;
  private final MenuItemCategoryRepository menuItemCategoryRepository;

  public MenuItemService(MenuItemRepository menuItemRepository,
      MenuItemCategoryRepository menuItemCategoryRepository) {
    this.menuItemRepository = menuItemRepository;
    this.menuItemCategoryRepository = menuItemCategoryRepository;
  }

  public List<MenuItem> filterItemByDietary(Set<String> dietaryRestrictions) {
    if (dietaryRestrictions == null || dietaryRestrictions.isEmpty()) {
      List<MenuItem> items = new ArrayList<>();
      menuItemRepository.findAll().forEach(items::add);
      return items;
    }

    List<MenuItem> items = new ArrayList<>();
    menuItemRepository.findAll().forEach(items::add);
    List<MenuItem> filteredItems = new ArrayList<>();

    for (MenuItem item : items) {
      for (DietaryRestriction restriction : item.getDietaryRestrictions()) {
        if (dietaryRestrictions.contains(restriction.getName())) {
          filteredItems.add(item);
          break;
        }
      }
    }

    return filteredItems;
  }

  public List<MenuItem> filterItemByAllergens(Set<String> Allergens) {
    if (Allergens == null || Allergens.isEmpty()) {
      List<MenuItem> items = new ArrayList<>();
      menuItemRepository.findAll().forEach(items::add);
      return items;
    }

    List<MenuItem> items = new ArrayList<>();
    menuItemRepository.findAll().forEach(items::add);
    List<MenuItem> filteredItems = new ArrayList<>();

    for (MenuItem item : items) {
      for (Integer allergenId : item.getAllergens()) {
        Allergen allergen = Allergen.fromId(allergenId);
        if (Allergens.contains(allergen.getName())) {
          filteredItems.add(item);
          break;
        }
      }
    }
    return filteredItems;
  }

  public MenuItem updateMenuItem(Long id, UpdateMenuItemRequest request) {

    MenuItem menuItem = menuItemRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Menu item not found"));

    if (request.getName() != null) {
      menuItem.setName(request.getName());
    }

    if (request.getDescription() != null) {
      menuItem.setDescription(request.getDescription());
    }

    if (request.getKcal() != null) {
      menuItem.setKcal(request.getKcal());
    }

    if (request.getPrice() != null) {
      menuItem.setPrice(request.getPrice());
    }

    return menuItemRepository.save(menuItem);
  }

  public MenuItem addMenuItem(UpdateMenuItemRequest request) {

    if (request.getName() == null || request.getName().isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }

    if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Price must be provided and >= 0");
    }

    if (request.getKcal() == null || request.getKcal() < 0) {
      throw new IllegalArgumentException("Kcal must be provided and >= 0");
    }

    if (request.getStatus() == null) {
      throw new IllegalArgumentException("Status is required");
    }

    if (request.getCategoryId() == null) {
      throw new IllegalArgumentException("Category is required");
    }

    MenuItemCategory category = menuItemCategoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Menu item category not found"));

    MenuItem menuItem = new MenuItem(request.getName(), request.getPrice(), request.getKcal(),
        request.getStatus(), category);

    menuItem.setDescription(request.getDescription());

    if (request.getFat() != null) {
      if (request.getFat() < 0) {
        throw new IllegalArgumentException("Fat cannot be negative");
      }
      menuItem.setFat(request.getFat());
    }

    if (request.getProtein() != null) {
      if (request.getProtein() < 0) {
        throw new IllegalArgumentException("Protein cannot be negative");
      }
      menuItem.setProtein(request.getProtein());
    }

    if (request.getCarbs() != null) {
      if (request.getCarbs() < 0) {
        throw new IllegalArgumentException("Carbs cannot be negative");
      }
      menuItem.setCarbs(request.getCarbs());
    }

    return menuItemRepository.save(menuItem);
  }


  public void saveMenuItemFile(Long menuItemId, MultipartFile file) throws IOException {
    MenuItem menuItem = menuItemRepository.findById(menuItemId)
        .orElseThrow(() -> new RuntimeException("Menu item not found"));

    // Verify that the file is an image
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("File must be an image (png, jpg, jpeg, gif, etc.)");
    }

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || !originalFilename.matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
      throw new IllegalArgumentException(
          "File must have a valid image extension: png, jpg, jpeg, gif, bmp");
    }

    // Extract file extension
    String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

    // Generate a unique filename using UUID
    String uniqueFilename = UUID.randomUUID().toString() + extension;

    // Save to project root: ../uploads/menu-items
    Path folder = Paths.get("../uploads/menu-items");
    Files.createDirectories(folder);

    Path filepath = folder.resolve(uniqueFilename);
    Files.write(filepath, file.getBytes());

    // Save the path to the MenuItem entity
    menuItem.setImage("/files/menu-items/" + uniqueFilename);
    menuItemRepository.save(menuItem);
  }
}
