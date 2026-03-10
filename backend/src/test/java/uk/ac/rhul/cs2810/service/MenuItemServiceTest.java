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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.rhul.cs2810.dto.UpdateMenuItemRequest;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.MenuItemCategory;
import uk.ac.rhul.cs2810.model.MenuItemStatus;
import uk.ac.rhul.cs2810.repository.MenuItemCategoryRepository;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;

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
    when(req.getPrice()).thenReturn(9.5);
    when(req.getKcal()).thenReturn(500.0);

    MenuItem result = service.updateMenuItem(1L, req);

    verify(item).setName("Burger");
    verify(item).setDescription("Nice");
    verify(item).setPrice(9.5);
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
    when(req.getPrice()).thenReturn(12.0);
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
    when(req.getPrice()).thenReturn(1.0);
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
    when(req.getPrice()).thenReturn(5.0);
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
    when(req.getPrice()).thenReturn(1.0);
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
}
