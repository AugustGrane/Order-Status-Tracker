export interface Item {
    id: number;
    name: string;
}

export interface StatusDefinition {
    id: number;
    name: string;
    image: string;
    description: string;
}

export interface OrderDetailsWithStatus {
    id: number;
    orderId: number;
    item: Item;
    itemAmount: number;
    product_type: string;
    currentStepIndex: number;
    differentSteps: StatusDefinition[];
    updated: Record<number, string>;
}

export interface OrderSummary {
    orderId: number;
    customerName: string;
    orderCreated: string;
    priority: boolean;
    totalItems: number;
}

export type SortDirection = 'asc' | 'desc';
export type SortableFields = 'orderId' | 'customerName' | 'orderCreated' | 'totalItems' | 'priority';

export interface DashboardState {
    orders: OrderSummary[];
    orderDetails: Record<number, OrderDetailsWithStatus[]>;
    expandedOrderId: number | null;
    loadingOrderId: number | null;
    loadingQueue: number[];
    isProcessingQueue: boolean;
    sortField: SortableFields;
    sortDirection: SortDirection;
}
