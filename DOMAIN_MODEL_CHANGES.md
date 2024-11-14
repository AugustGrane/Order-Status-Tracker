# Domain Model Changes and Architecture Overview

## Table of Contents
1. [Architecture Layers](#architecture-layers)
2. [Pattern Implementations](#pattern-implementations)
3. [Usage Locations](#usage-locations)
4. [Model vs Domain](#model-vs-domain)
5. [Benefits](#benefits)

## Architecture Layers

### Domain Layer (`/domain`)
Contains business logic and rules:
- Value Objects (OrderId, CustomerInfo)
- Domain Events (OrderCreatedEvent, ItemStatusChangedEvent)
- Commands (UpdateItemStatusCommand, UpdateProductTypeCommand)
- Specifications (OrderInvariantsSpecification, HasItemSpecification)
- Domain Exceptions (InvalidStatusTransitionException)
- Domain Objects (Order, OrderItem, OrderTimeline)

### Model Layer (`/model`)
Contains persistence entities:
- Database entities (Order, OrderDetails)
- JPA mappings
- No business logic
- Maps to/from domain objects

### Service Layer (`/service`)
Orchestrates domain objects and persistence:
- OrderService: Creates and manages orders
- OrderProgressService: Handles orderModel status changes
- ProductTypeService: Manages product type transitions
- Translates between domain and model objects

### Controller Layer (`/controller`)
Handles HTTP requests and responses:
- OrderController: Exposes orderModel operations
- WebhookController: Handles external integrations
- Works with DTOs and delegates to services

## Pattern Implementations

### Command Pattern
Located in `domain/command/`:
```java
public interface OrderCommand {
    void execute(Order orderModel);
}
```

Concrete implementations:
1. `UpdateItemStatusCommand`: Updates item status
   ```java
   public class UpdateItemStatusCommand implements OrderCommand {
       private final Long itemId;
       private final OrderStatus newStatus;
       
       @Override
       public void execute(Order orderModel) {
           orderModel.updateItemStatus(itemId, newStatus);
       }
   }
   ```

2. `UpdateProductTypeCommand`: Changes product type
   ```java
   public class UpdateProductTypeCommand implements OrderCommand {
       private final Long itemId;
       private final ProductTypeTransition transition;
       
       @Override
       public void execute(Order orderModel) {
           orderModel.updateItemProductType(itemId, transition);
       }
   }
   ```

### Specification Pattern
Located in `domain/specification/`:
```java
public interface OrderSpecification {
    boolean isSatisfiedBy(Order orderModel);
    OrderSpecification and(OrderSpecification other);
    OrderSpecification or(OrderSpecification other);
    OrderSpecification not();
}
```

Concrete implementations:
1. `OrderInvariantsSpecification`: Validates orderModel rules
   ```java
   public class OrderInvariantsSpecification implements OrderSpecification {
       @Override
       public boolean isSatisfiedBy(Order orderModel) {
           return orderModel.getId() != null &&
                  orderModel.getCustomerInfo() != null &&
                  orderModel.getTimeline() != null;
       }
   }
   ```

2. `HasItemSpecification`: Checks item existence
   ```java
   public class HasItemSpecification implements OrderSpecification {
       private final Long itemId;
       
       @Override
       public boolean isSatisfiedBy(Order orderModel) {
           return orderModel.findItem(itemId).isPresent();
       }
   }
   ```

## Model vs Domain

### Domain Objects (domain/*)
- Rich domain model with behavior
- Business logic and rules
- Immutable where possible
- No persistence concerns
- Example:
  ```java
  public class Order {
      private final OrderId id;
      private final CustomerInfo customerInfo;
      private final Set<OrderItem> items;
      
      public void updateItemStatus(Long itemId, OrderStatus newStatus) {
          // Business logic here
      }
  }
  ```

### Model Objects (model/*)
- JPA entities
- Persistence focused
- Mutable for ORM
- No business logic
- Example:
  ```java
  @Entity
  public class Order {
      @Id
      private Long id;
      private String customerName;
      private boolean priority;
      
      // Getters and setters
  }
  ```

### Translation Layer (in Services)
Services translate between domain and model:
```java
@Service
public class OrderService {
    public gruppe2.backend.model.OrderModel createOrder(OrderDTO dto) {
        // Create domain object
        gruppe2.backend.domain.Order domainOrder = OrderFactory.createOrder(...);
        
        // Validate using domain rules
        if (!invariants.isSatisfiedBy(domainOrder)) {
            throw new IllegalStateException("Invalid orderModel");
        }
        
        // Convert to model object for persistence
        gruppe2.backend.model.OrderModel orderModelEntity = new gruppe2.backend.model.OrderModel();
        orderModelEntity.setId(domainOrder.getId().getValue());
        orderModelEntity.setCustomerName(domainOrder.getCustomerInfo().getName());
        // ... more mapping ...
        
        return orderRepository.save(orderModelEntity);
    }
}
```

## Benefits

1. **Clear Separation of Concerns**
   - Domain logic isolated in domain layer
   - Persistence concerns in model layer
   - Services handle translation
   - Controllers manage HTTP concerns

2. **Rich Domain Model**
   - Business logic where it belongs
   - Domain objects enforce invariants
   - Commands encapsulate operations
   - Specifications validate rules

3. **Maintainable Persistence**
   - Clean model objects
   - Simple ORM mapping
   - No business logic in entities
   - Clear persistence boundaries

4. **Flexible Architecture**
   - Domain changes don't affect persistence
   - New commands without changing existing code
   - Business rules composed via specifications
   - Events enable loose coupling

The architecture achieves:
- Clear boundaries between concerns
- Rich domain model with behavior
- Clean persistence layer
- Maintainable and testable code
