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
    productTypeName: string;
    currentStepIndex: number;
    differentSteps: StatusDefinition[];
    updated: Record<number, string>;
}
