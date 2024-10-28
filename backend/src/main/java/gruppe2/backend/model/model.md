# Database Schema

## Orders
| Column               | Type                                         |
|----------------------|----------------------------------------------|
| id                   | PRIMARY KEY                                  |
| customer_name        | string                                       |
| priority             | Boolean                                      |
| notes                | String                                       |
| order_created        | datetime                                     |
| total_estimated_time | int                                          |
| Item_mapping         | Map<FK.Item_id, amount>                      |
| Product_type_mapping | Map<FK.OrderProductType_id, int currentStep> |



## OrderProductTypes
| Column         | Type                                    |
|----------------|-----------------------------------------|
| id             | PRIMARY KEY                             |
| order_id       | FOREIGN KEY -> orders.id                |
| name           | string                                  |
| differentSteps | statusDefinitions.id[]                  |
| updated        | Map<FK.status_definitions.id, datetime> |


## ProductTypes
| Column         | Type                                  |
|----------------|---------------------------------------|
| id             | PRIMARY KEY                           |
| name           | string                                |
| differentSteps | FOREIGN KEY -> statusDefinitions.id[] |


## Status Definitions
| Column      | Type                           |
|-------------|--------------------------------|
| id          | PRIMARY KEY                    |
| name        | string                         |
| description | string                         |
| image       | Link                           |


## Items
| Column         | Type                           |
|----------------|--------------------------------|
| id             | PRIMARY KEY   // Artikel nr.   |
| name           | string                         |
| product        | string                         |
| productType_id | FOREIGN KEY -> ProductTypes.id |


*Note: All relationships use proper indexing for efficient querying.*