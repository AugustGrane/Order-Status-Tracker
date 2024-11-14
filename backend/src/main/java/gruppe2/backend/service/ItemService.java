package gruppe2.backend.service;

import gruppe2.backend.dto.ItemDTO;
import gruppe2.backend.domain.command.CreateItemCommand;
import gruppe2.backend.model.Item;
import gruppe2.backend.repository.ItemRepository;
import gruppe2.backend.repository.ProductTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing Item entities.
 * This service provides methods for creating and retrieving items.
 */
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProductTypeRepository productTypeRepository;

    public ItemService(ItemRepository itemRepository, ProductTypeRepository productTypeRepository) {
        this.itemRepository = itemRepository;
        this.productTypeRepository = productTypeRepository;
    }

    /**
     * Creates a new Item based on the provided ItemDTO.
     *
     * @param itemDTO The DTO containing item creation data
     * @return The created Item entity
     */
    @Transactional
    public Item createItem(ItemDTO itemDTO) {
        CreateItemCommand command = new CreateItemCommand(itemDTO, itemRepository, productTypeRepository);
        return command.execute();
    }

    /**
     * Finds an Item by its ID.
     *
     * @param itemId The ID of the item to find
     * @return The found Item entity
     * @throws RuntimeException if the item is not found
     */
    @Transactional(readOnly = true)
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
    }

    /**
     * Retrieves all items.
     *
     * @return List of all items
     */
    @Transactional(readOnly = true)
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    /**
     * Finds an Item by its name.
     *
     * @param name The name of the item to find
     * @return Optional containing the item if found
     */
    @Transactional(readOnly = true)
    public Optional<Item> findByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .findFirst();
    }
}
