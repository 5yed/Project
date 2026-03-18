package uk.ac.rhul.cs2810.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.rhul.cs2810.dto.UpdateMenuItemRequest;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.service.MenuItemService;

@RestController
@RequestMapping("/api/menuItems")
public class MenuItemController {

  private final MenuItemService menuItemService;

  public MenuItemController(MenuItemService menuItemService) {
    this.menuItemService = menuItemService;
  }

  @PutMapping("/{id}")
  public MenuItem updateMenuItem(@PathVariable Long id,
      @RequestBody UpdateMenuItemRequest request) {

    return menuItemService.updateMenuItem(id, request);
  }

  @GetMapping("/filter/dietary")
  public List<MenuItem> filterByDietary(@RequestParam(required = false) Set<String> dietary) {
    return menuItemService.filterItemByDietary(dietary);
  }

  @GetMapping("/filter/allergens")
  public List<MenuItem> filterByAllergen(@RequestParam(required = false) Set<String> allergen) {
    return menuItemService.filterItemByAllergens(allergen);
  }

  @PostMapping("/{id}/upload")
  public ResponseEntity<String> uploadFile(@PathVariable Long id,
      @RequestParam("file") MultipartFile file) {

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("No file selected");
    }

    try {
      menuItemService.saveMenuItemFile(id, file);
      return ResponseEntity.ok("File uploaded successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to upload file");
    }
  }

  @PostMapping
  public MenuItem addMenuItem(@RequestBody UpdateMenuItemRequest request) {
    return menuItemService.addMenuItem(request);
  }

}
