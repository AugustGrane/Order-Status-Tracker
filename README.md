# Order Status Tracker

A full-stack application built with SvelteKit frontend and Spring Boot backend.

## Project Structure

```
.
├── frontend/          # SvelteKit frontend application
│   ├── src/          # Source files
│   ├── static/       # Static assets
│   └── ...
│
└── backend/          # Spring Boot backend application
    ├── src/          # Source files
    └── ...
```

## Architecture Overview

The backend follows a domain-driven design (DDD) approach with clear separation of concerns and consistent use of design patterns:

### Controller Layer
- Entry point for all HTTP requests
- Handles request/response mapping
- Input validation
- Routes requests to appropriate services
- Key components:
  - `OrderController`: Handles order-related endpoints
  - `WebhookController`: Processes incoming webhooks

### Service Layer
- Orchestrates domain operations using command pattern
- Manages transactions and persistence
- Delegates business logic to domain layer
- Key services:
  - `OrderService`: Executes order commands and manages persistence
  - `WebhookService`: Converts webhooks to domain commands
  - `ItemService`: Handles item operations
  - `ProductTypeService`: Manages product types
  - `OrderProgressService`: Tracks order progress

### Mapper Layer
- Handles object transformations between different layers
- Centralizes mapping logic for better maintainability
- Key mappers:
  - `OrderMapper`: Maps between domain Order and model Order/DTOs
  - `OrderDetailsMapper`: Maps OrderDetails to various DTOs
  - `WebhookMapper`: Maps webhook payloads to domain objects

### Domain Layer
- Contains core business logic and rules
- Implements command pattern for operations
- Uses factory pattern for object creation
- Enforces invariants through specifications
- Key components:
  - Commands:
    - `CreateOrderCommand`: Creates new orders
    - `CreateItemCommand`: Creates new items
    - `CreateProductTypeCommand`: Creates product types
    - `CreateStatusDefinitionCommand`: Creates status definitions
    - `UpdateItemStatusCommand`: Updates item status
    - `UpdateProductTypeCommand`: Changes product types
    - `ProcessWebhookCommand`: Processes webhook payloads
    - `SetupOrderDetailsCommand`: Sets up order details
  - Factory:
    - `OrderFactory`: Creates domain objects consistently
  - Specifications:
    - `OrderInvariantsSpecification`: Validates business rules
  - Entities:
    - `Order`: Rich domain model with behavior
    - `OrderItem`: Represents items in an order
  - Value Objects:
    - `OrderId`: Encapsulates order identity
    - `CustomerInfo`: Contains customer details

### Model/Repository Layer
- Handles data persistence
- Maps domain objects to database entities
- Key components:
  - Entities:
    - `OrderEntity`: Database representation of orders
    - `OrderDetails`: Stores order item details
  - Repositories:
    - `OrderRepository`: Persists orders
    - `ItemRepository`: Manages items
    - `ProductTypeRepository`: Handles product types

## Layer Interactions

1. Controllers receive requests and convert to DTOs
2. Services use mappers to transform data between layers
3. Services create appropriate commands
4. Commands execute on domain objects
5. Domain objects enforce business rules
6. Services use mappers to transform domain objects for persistence
7. Services handle persistence through repositories

### Command Pattern Flow
1. Service layer creates a command with necessary data
2. Command executes on a domain object or performs a specific operation
3. Domain object updates its state (if applicable)
4. Service layer persists the changes

### Factory Pattern Usage
- `OrderFactory` creates consistent domain objects
- Used by both `OrderService` and `WebhookService`
- Ensures domain objects are created with valid state

### Specification Pattern
- Validates business rules
- Used before persisting changes
- Ensures domain invariants are maintained

### Mapper Pattern Usage
- Centralizes transformation logic
- Provides clean separation between layers
- Makes data transformations maintainable and testable
- Reduces coupling between layers

## Performance Optimization

### Database Performance

The application has been optimized for fast dashboard loading times, achieving significant performance improvements:

#### Performance Metrics
- Initial Dashboard Loading: ~3680ms
- Optimized Dashboard Loading: ~565ms
- Performance Improvement: ~89% reduction in loading time

#### Database Indexing Strategy
We maintain a simple but effective indexing strategy:
```sql
-- Primary indexes
CREATE INDEX IF NOT EXISTS idx_status_definitions_id ON status_definitions(id);
CREATE INDEX IF NOT EXISTS idx_status_definitions_name ON status_definitions(name);
CREATE INDEX IF NOT EXISTS idx_order_details_order_id ON order_details(order_id);
```

These indexes were chosen after careful performance testing and provide the best balance between query performance and maintenance overhead.

### Database Configuration

#### Connection Details
The application uses Neon PostgreSQL as the database provider. Configuration is managed through environment variables:

```properties
NEON_DB_URL=jdbc:postgresql://<host>/gtrykdb?sslmode=require
NEON_DB_USERNAME=gtrykdb_owner
```

#### Migration Management
We use Flyway for database migrations. Key commands:

```bash
# Run migrations
./mvnw flyway:migrate

# Repair migration history (if needed)
./mvnw flyway:repair
```

### Technology Stack
- Spring Boot: 3.3.5
- Java: 22.0.2
- Database: PostgreSQL 16.5 (Neon)
- ORM: Hibernate 6.5.3
- Migration Tool: Flyway 10.10.0

### Performance Best Practices

1. **Query Optimization**
   - Use appropriate indexes for frequently queried columns
   - Keep indexes simple and focused
   - Regular monitoring of query performance

2. **Database Design**
   - Normalized schema design
   - Appropriate use of foreign keys
   - Regular database maintenance

3. **Application Layer**
   - Efficient use of Hibernate
   - Proper transaction management
   - Regular performance monitoring

### Monitoring and Maintenance

1. **Performance Monitoring**
   - Regular dashboard loading time checks
   - Database query performance analysis
   - Resource utilization monitoring

2. **Database Maintenance**
   - Regular index maintenance
   - Periodic vacuum operations
   - Query plan analysis

3. **Troubleshooting**
   - Check slow query logs
   - Monitor database connections
   - Analyze application metrics

## Setup Instructions

### Backend (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend (SvelteKit)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on `http://localhost:5173`

## Development

- Frontend development server: `http://localhost:5173`
- Backend API: `http://localhost:8080`

## Building for Production

### Backend
```bash
cd backend
./mvnw clean package
```

### Frontend
```bash
cd frontend
npm run build
```

## Contributing

When contributing to this project, please consider the following:

1. **Performance Impact**
   - Test any changes against the performance baseline
   - Document performance implications
   - Consider index impact for schema changes

2. **Database Changes**
   - Always use Flyway migrations for schema changes
   - Test migrations in a staging environment
   - Document any new indexes or constraints

3. **Code Quality**
   - Follow existing architectural patterns
   - Maintain separation of concerns
   - Add appropriate tests

## Troubleshooting

### Common Issues

1. **Slow Dashboard Loading**
   - Check database connection pool settings
   - Verify index usage with EXPLAIN ANALYZE
   - Monitor database resource utilization

2. **Migration Issues**
   - Use `flyway:repair` for migration conflicts
   - Check migration versioning
   - Verify database credentials

3. **Performance Degradation**
   - Review recent changes
   - Check database statistics
   - Analyze query plans

## License

[Add your license information here]
