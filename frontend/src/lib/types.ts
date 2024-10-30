export interface Item {
    id: number;
    name: string;
}

export interface StatusDefinition {
    id: number;
    name: string;
    image: string;
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
