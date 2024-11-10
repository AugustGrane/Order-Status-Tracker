erDiagram
    Orders {
        Long id PK
        String customerName
        Boolean priority
        String notes
        LocalDateTime orderCreated
        Integer totalEstimatedTime
    }

    Items {
        Long id PK
        String itemImage
        String name
        Long productTypeId FK
    }

    ProductTypes {
        Long id PK
        String name
    }

    OrderDetails {
        Long id PK
        Long orderId FK
        Long itemId FK
        Integer currentStepIndex
        Integer itemAmount
        String productType
    }

    StatusDefinitions {
        Long id PK
        String description
        String image
        String name
    }

    OrderProductTypeSteps {
        Long orderProductTypeId FK
        Long stepId FK
        Integer stepOrder
    }

    OrderProductTypeUpdated {
        Long orderProductTypeId FK
        LocalDateTime updated
        Long statusDefinitionId FK
    }

    OrderSteps {
        Long orderId FK
        Long stepId FK
        Integer stepOrder
    }

    ProductTypeSteps {
        Long productTypeId FK
        Long stepId FK
        Integer stepOrder
    }

    Orders ||--o{ OrderDetails : "contains"
    Items ||--o{ OrderDetails : "used in"
    ProductTypes ||--o{ Items : "categorizes"
    ProductTypes ||--o{ ProductTypeSteps : "has steps"
    StatusDefinitions ||--o{ OrderProductTypeUpdated : "tracks status"
    Orders ||--o{ OrderSteps : "has steps"
    ProductTypes ||--o{ OrderProductTypeSteps : "defines steps"
    StatusDefinitions ||--o{ ProductTypeSteps : "defines steps"
```

## Database Structure Explanation

### Core Tables

1. **Orders**
   - Main table storing order information
   - Contains customer details, priority, and timestamps
   - Links to OrderDetails for specific items in the order

2. **Items**
   - Stores available items that can be ordered
   - Each item is associated with a ProductType
   - Contains item name and image information

3. **ProductTypes**
   - Defines the different types of products available
   - Acts as a template for product processing steps
   - Each product type has its own sequence of steps

4. **StatusDefinitions**
   - Stores all possible status types
   - Contains descriptions and images for each status
   - Used by both product types and orders

### Step Management

The system uses a cloning mechanism for steps, allowing each order to have its own independent progression:

1. **Template Steps**
   - `ProductTypeSteps`: Defines the template/base steps for each product type
   - These are the master steps that new orders will copy from

2. **Order-Specific Steps**
   - `OrderSteps`: Contains the actual steps for each specific order
   - `OrderProductTypeSteps`: Stores the cloned steps when an order is created
   - This allows each order to progress independently through its steps
   - Changes to the template steps (ProductTypeSteps) won't affect existing orders

### Status Tracking

Status updates are tracked through:

1. **Order Progress**
   - `OrderDetails.currentStepIndex`: Tracks the current step for each order item
   - `OrderProductTypeUpdated`: Records timestamps when steps are completed
   - Maintains a complete history of when each step was reached

### How It Works

1. When a new order is created:
   - Entry created in `Orders` table
   - `OrderDetails` created for each item in the order
   - Steps are cloned from `ProductTypeSteps` into `OrderSteps`
   - Initial status is recorded in `OrderProductTypeUpdated`

2. As the order progresses:
   - Current step is tracked in `OrderDetails.currentStepIndex`
   - Status changes are recorded in `OrderProductTypeUpdated`
   - Each order maintains its own independent progression

3. This structure ensures:
   - Each order has its own copy of steps
   - Orders can be tracked independently
   - Product type templates can be modified without affecting existing orders
   - Complete history of status changes is maintained through OrderProductTypeUpdated
