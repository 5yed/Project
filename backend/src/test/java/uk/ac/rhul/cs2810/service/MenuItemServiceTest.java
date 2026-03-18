package uk.ac.rhul.cs2810.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.rhul.cs2810.dto.UpdateMenuItemRequest;
import uk.ac.rhul.cs2810.model.Allergen;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.DietaryRestriction;
import uk.ac.rhul.cs2810.model.MenuItemCategory;
import uk.ac.rhul.cs2810.model.MenuItemStatus;
import uk.ac.rhul.cs2810.repository.MenuItemCategoryRepository;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.service.MenuItemService;

/**
 * Unit tests for MenuItemService.
 *
 * Tests menu item update, creation, validation and image upload behaviour. Dependencies are mocked
 * so only service logic is tested.
 */
class MenuItemServiceTest {

  @Mock
  private MenuItemRepository menuItemRepository;

  @Mock
  private MenuItemCategoryRepository categoryRepository;

  @InjectMocks
  private MenuItemService service;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests updating an existing menu item.
   */
  @Test
  void updateMenuItem_success() {

    MenuItem item = mock(MenuItem.class);
    UpdateMenuItemRequest req = mock(UpdateMenuItemRequest.class);

    when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    when(req.getName()).thenReturn("Burger");
    when(req.getDescription()).thenReturn("Nice");
    when(req.getPrice()).thenReturn(new BigDecimal("9.50"));
    when(req.getKcal()).thenReturn(500.0);

    MenuItem result = service.updateMenuItem(1L, req);

    verify(item).setName("Burger");
    verify(item).setDescription("Nice");
    verify(item).setPrice(new BigDecimal("9.50"));
    verify(item).setKcal(500.0);

    assertEquals(item, result);
  }

  /**
   * Tests update throws when item missing.
   */
  @Test
  void updateMenuItem_notFound() {

    when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

    UpdateMenuItemRequest req = mock(UpdateMenuItemRequest.class);

    assertThrows(RuntimeException.class, () -> service.updateMenuItem(99L, req));
  }

  /**
   * Tests successful creation of menu item.
   */
  @Test
  void addMenuItem_success() {

    UpdateMenuItemRequest req = mock(UpdateMenuItemRequest.class);
    MenuItemCategory category = mock(MenuItemCategory.class);

    when(req.getName()).thenReturn("Pizza");
    when(req.getPrice()).thenReturn(new BigDecimal("12.0"));
    when(req.getKcal()).thenReturn(800.0);
    when(req.getStatus()).thenReturn(MenuItemStatus.AVAILABLE);
    when(req.getCategoryId()).thenReturn(2L);

    when(req.getDescription()).thenReturn("Good");
    when(req.getFat()).thenReturn(10.0);
    when(req.getProtein()).thenReturn(20.0);
    when(req.getCarbs()).thenReturn(30.0);

    when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
    when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    MenuItem result = service.addMenuItem(req);

    assertNotNull(result);
    verify(menuItemRepository).save(any());
  }

  /**
   * Tests validation: missing name.
   */
  @Test
  void addMenuItem_missingName() {

    UpdateMenuItemRequest req = mock(UpdateMenuItemRequest.class);

    when(req.getName()).thenReturn(null);
    when(req.getPrice()).thenReturn(new BigDecimal("1.0"));
    when(req.getKcal()).thenReturn(1.0);
    when(req.getStatus()).thenReturn(MenuItemStatus.AVAILABLE);
    when(req.getCategoryId()).thenReturn(1L);

    assertThrows(IllegalArgumentException.class, () -> service.addMenuItem(req));
  }

  /**
   * Tests validation: category not found.
   */
  @Test
  void addMenuItem_categoryNotFound() {

    UpdateMenuItemRequest req = mock(UpdateMenuItemRequest.class);

    when(req.getName()).thenReturn("Soup");
    when(req.getPrice()).thenReturn(new BigDecimal("5.0"));
    when(req.getKcal()).thenReturn(200.0);
    when(req.getStatus()).thenReturn(MenuItemStatus.AVAILABLE);
    when(req.getCategoryId()).thenReturn(3L);

    when(categoryRepository.findById(3L)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> service.addMenuItem(req));
  }

  /**
   * Tests validation: negative fat not allowed.
   */
  @Test
  void addMenuItem_negativeFat() {

    UpdateMenuItemRequest req = mock(UpdateMenuItemRequest.class);
    MenuItemCategory category = mock(MenuItemCategory.class);

    when(req.getName()).thenReturn("Food");
    when(req.getPrice()).thenReturn(new BigDecimal("1.0"));
    when(req.getKcal()).thenReturn(1.0);
    when(req.getStatus()).thenReturn(MenuItemStatus.AVAILABLE);
    when(req.getCategoryId()).thenReturn(1L);
    when(req.getFat()).thenReturn(-5.0);

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    assertThrows(IllegalArgumentException.class, () -> service.addMenuItem(req));
  }

  /**
   * Tests successful image upload.
   */
  @Test
  void saveMenuItemFile_success() throws IOException {

    MenuItem item = mock(MenuItem.class);
    MultipartFile file = mock(MultipartFile.class);

    when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    when(file.getContentType()).thenReturn("image/png");
    when(file.getOriginalFilename()).thenReturn("pic.png");
    when(file.getBytes()).thenReturn(new byte[] {1, 2, 3});

    service.saveMenuItemFile(1L, file);

    verify(item).setImage(startsWith("/files/menu-items/"));
    verify(menuItemRepository).save(item);
  }

  /**
   * Tests upload fails for non-image file.
   */
  @Test
  void saveMenuItemFile_invalidType() {

    MenuItem item = mock(MenuItem.class);
    MultipartFile file = mock(MultipartFile.class);

    when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
    when(file.getContentType()).thenReturn("text/plain");

    assertThrows(IllegalArgumentException.class, () -> service.saveMenuItemFile(1L, file));
  }

  @Test
  public void testDietaryFiltering() {
    DietaryRestriction vegan = new DietaryRestriction("vegan");
    DietaryRestriction glutenFree = new DietaryRestriction("gluten-free");

    MenuItem pizza = new MenuItem();
    MenuItem lettuce = new MenuItem();
    MenuItem egg = new MenuItem();

    pizza.setName("pizza");
    pizza.addDietaryRestriction(vegan);
    pizza.addDietaryRestriction(glutenFree);

    lettuce.setName("lettuce");
    lettuce.addDietaryRestriction(vegan);

    egg.setName("egg");

    when(menuItemRepository.findAll()).thenReturn(List.of(pizza, lettuce, egg));
    List<MenuItem> filteredList = service.filterItemByDietary(Set.of("vegan", "gluten-free"));

    assertEquals(2, filteredList.size());
    assertEquals("pizza", filteredList.get(0).getName());
  }

  @Test
  public void testAllergenFiltering() {
    Allergen nuts = Allergen.NUTS;
    Allergen eggs = Allergen.EGGS;
    Allergen milk = Allergen.MILK;

    MenuItem cake = new MenuItem();
    MenuItem eggSandwich = new MenuItem();
    MenuItem lettuce = new MenuItem();

    cake.setName("cake");
    cake.addAllergen(nuts.getId());
    cake.addAllergen(eggs.getId());
    cake.addAllergen(milk.getId());

    eggSandwich.setName("egg sandwich");
    eggSandwich.addAllergen(eggs.getId());
    eggSandwich.addAllergen(milk.getId());

    lettuce.setName("lettuce");

    when(menuItemRepository.findAll()).thenReturn(List.of(cake, eggSandwich, lettuce));
    List<MenuItem> filteredList = service.filterItemByAllergens(Set.of("nuts", "eggs", "milk"));

    assertEquals(2, filteredList.size());
    assertEquals("cake", filteredList.get(0).getName());
  }
}
