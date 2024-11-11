package gruppe2.backend.service;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.model.Item;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;

    public ItemService(ItemRepository itemRepository, ProductTypeRepository productTypeRepository) {
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
    }

    public Item createItem(ItemDTO itemDTO) {
        validateProductType(itemDTO.productTypeId());

        Item item = new Item();
        item.setId(itemDTO.id());
        item.setName(itemDTO.name());
        item.setProductTypeId(itemDTO.productTypeId());
        item.setImage(itemDTO.item_image());

        return itemRepository.save(item);
    }

    private void validateProductType(Long productTypeId) {
        productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new RuntimeException("Product type not found"));
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
    }
}
