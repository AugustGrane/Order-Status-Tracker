# Comprehensive Guide to Code Structure Improvements

## Table of Contents
1. [Previous System Structure](#previous-system-structure)
2. [New System Architecture](#new-system-architecture)
3. [Detailed Component Overview](#detailed-component-overview)
4. [Error Handling System](#error-handling-system)
5. [Event Management System](#event-management-system)
6. [Implementation Examples](#implementation-examples)
7. [System Benefits](#system-benefits)
8. [Migration Guide](#migration-guide)

## Previous System Structure

The original codebase had several structural limitations that affected maintainability and reliability:

### Service Layer Issues
```java
@Service
public class OrderService {
    public void moveToNextStep(Long id) {
        OrderDetails orderDetails = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));
        
        if (orderDetails.getCurrentStepIndex() >= orderDetails.getDifferentSteps().length - 1) {
            throw new IllegalStateException("Already at final step");
        }
        
        orderDetails.setCurrentStepIndex(orderDetails.getCurrentStepIndex() + 1);
        repository.save(orderDetails);
    }
}
```

Key problems:
1. Business logic mixed with data access
2. No validation encapsulation
3. Generic error handling
4. Difficult to extend or modify
5. No clear separation of concerns
6. Limited reusability of components

## New System Architecture

The improved system implements a structured, component-based architecture:

### Core Domain Model
```java
public class Order {
    private final Long id;
    private final CustomerInfo customerInfo;
    private final Set<OrderItem> items;
    private final OrderTimeline timeline;
    private final OrderEstimation estimation;
    private final List<OrderEvent> events;

    // Builder pattern for complex object creation
    public static class Builder {
        // Builder implementation
    }
}
```

## Detailed Component Overview

### 1. CustomerInfo Component
```java
public class CustomerInfo {
    private final String name;
    private final String notes;
    private final boolean priority;

    public CustomerInfo(String name, String notes, boolean priority) {
        validateName(name);
        this.name = name;
        this.notes = notes;
        this.priority = priority;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
    }
}
```

Purpose:
- Encapsulates customer data
- Ensures data validity
- Provides immutable state
- Implements value object pattern

### 2. OrderItem Component
```java
public class OrderItem {
    private final Item item;
    private final int quantity;
    private final String productTypeName;
    private final OrderStatus status;

    public boolean canChangeStatus(OrderStatus newStatus) {
        if (isGenericType()) {
            return false;
        }
        return Math.abs(newStatus.getCurrentStepIndex() - status.getCurrentStepIndex()) == 1;
    }

    public OrderItem withStatus(OrderStatus newStatus) {
        validateStatusTransition(newStatus);
        return new OrderItem(item, quantity, productTypeName, newStatus);
    }
}
```

Features:
- Manages item state
- Validates status transitions
- Implements immutable updates
- Encapsulates business rules

### 3. OrderStatus Component
```java
public class OrderStatus {
    private final Long[] steps;
    private final int currentStepIndex;
    private final Map<Long, LocalDateTime> statusUpdates;

    public boolean canMoveToNextStep() {
        return currentStepIndex < steps.length - 1;
    }

    public OrderStatus moveToNextStep() {
        validateNextStep();
        return new OrderStatus(steps, currentStepIndex + 1, updateStatusTimestamp());
    }
}
```

Responsibilities:
- Status progression control
- Status history tracking
- Timestamp management
- Validation rules

## Error Handling System

### 1. Base Exception
```java
public abstract class OrderException extends RuntimeException {
    protected OrderException(String message) {
        super(message);
    }
}
```

### 2. Specific Exceptions
```java
public class InvalidStatusTransitionException extends OrderException {
    private final Long itemId;
    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;

    public InvalidStatusTransitionException(Long itemId, OrderStatus current, OrderStatus target) {
        super(formatMessage(itemId, current, target));
        this.itemId = itemId;
        this.currentStatus = current;
        this.targetStatus = target;
    }
}
```

Benefits:
- Clear error identification
- Detailed error context
- Proper error hierarchy
- Improved debugging

## Event Management System

### 1. Base Event
```java
public abstract class OrderEvent {
    private final Long orderId;
    private final LocalDateTime timestamp;

    public abstract String getEventType();
}
```

### 2. Specific Events
```java
public class ItemStatusChangedEvent extends OrderEvent {
    private final Long itemId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;

    @Override
    public String getEventType() {
        return "ITEM_STATUS_CHANGED";
    }
}
```

Features:
- Event tracking
- Audit trail
- System monitoring
- Integration capabilities

## Implementation Examples

### Creating Orders
```java
// Create customer information
CustomerInfo customerInfo = new CustomerInfo(
    "John Smith",
    "Priority handling required",
    true
);

// Create order timeline
OrderTimeline timeline = new OrderTimeline(
    LocalDateTime.now(),
    customerInfo.isPriority()
);

// Create order estimation
OrderEstimation estimation = new OrderEstimation(
    items,
    processingTimes,
    customerInfo.isPriority()
);

// Build the order
Order order = new Order.Builder()
    .withId(generatedId)
    .withCustomerInfo(customerInfo)
    .withTimeline(timeline)
    .withEstimation(estimation)
    .build();
```

### Updating Status
```java
try {
    order.updateItemStatus(itemId, newStatus);
} catch (InvalidStatusTransitionException e) {
    // Handle invalid transition
    logger.error("Status transition failed: {}", e.getMessage());
} catch (OrderException e) {
    // Handle other order-related errors
    logger.error("Order operation failed: {}", e.getMessage());
}
```

## System Benefits

### 1. Improved Code Organization
- Clear component boundaries
- Single responsibility principle
- Easy to understand structure
- Modular design

### 2. Enhanced Reliability
- Comprehensive validation
- Proper error handling
- Consistent behavior
- Traceable operations

### 3. Better Maintainability
- Isolated components
- Clear dependencies
- Easy to test
- Simple to extend

### 4. Operational Improvements
- Clear audit trail
- Better monitoring
- Easier debugging
- Performance optimization opportunities

## Migration Guide

### 1. Data Migration
```java
// Convert old order format to new domain model
public Order convertToNewFormat(OldOrder oldOrder) {
    CustomerInfo customerInfo = new CustomerInfo(
        oldOrder.getCustomerName(),
        oldOrder.getNotes(),
        oldOrder.isPriority()
    );
    
    // Continue conversion process
}
```

### 2. Service Updates
```java
// Update service methods to use new domain model
@Service
public class OrderService {
    @Transactional
    public Order createOrder(OrderDTO dto) {
        // Create domain objects
        CustomerInfo customerInfo = new CustomerInfo(
            dto.customerName(),
            dto.notes(),
            dto.priority()
        );
        
        // Build and return order
    }
}
```

The new system provides a robust, maintainable, and extensible foundation for the Order Status Tracker application. Each component has clear responsibilities and boundaries, making the system easier to understand, maintain, and extend.
