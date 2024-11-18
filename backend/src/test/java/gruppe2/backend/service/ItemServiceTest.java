package gruppe2.backend.service;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.model.ProductType;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)

public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ProductTypeRepository productTypeRepository;


    @Test
    void findById_WithValidId_ShouldReturnItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void createItem_ShouldCreateAndReturnItem() {
        ItemDTO itemDTO = new ItemDTO("Test Item", 1001L, 1L, "src", false);

        ProductType productType = new ProductType();
        productType.setId(1L);
        productType.setName("Test Type");
        when(productTypeRepository.findById(1L)).thenReturn(Optional.of(productType));

        Item savedItem = new Item();
        savedItem.setName("Test Item");
        savedItem.setId(1001L);
        savedItem.setProductTypeId(1L);
        savedItem.setImage("src");

        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.createItem(itemDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getProductTypeId()).isEqualTo(1L);
        assertThat(result.getImage()).isEqualTo("src");

        verify(itemRepository, times(1)).save(any(Item.class));
        verify(productTypeRepository, times(1)).findById(1L);
    }
}
