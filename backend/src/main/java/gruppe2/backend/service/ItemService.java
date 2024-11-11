package gruppe2.backend.service;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.domain.command.CreateItemCommand;
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
        CreateItemCommand command = new CreateItemCommand(itemDTO, itemRepository, productTypeRepository);
        return command.execute();
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
    }
}
