import { writable } from 'svelte/store';
import type { OrderDetailsWithStatus } from '$lib/types';

// Create a store to hold our orders
const { subscribe, set, update } = writable<{
    [orderId: string]: OrderDetailsWithStatus[] | null
}>({});

// Export our store with custom methods
export const orderStore = {
    subscribe,

    // Method to fetch and store order data
    async getOrder(orderId: string) {
        // Try to get from store first
        let orders: { [key: string]: OrderDetailsWithStatus[] | null };
        subscribe(value => { orders = value; })();

        // If we have it in cache and it's not null, return it
        if (orders[orderId]) {
            return orders[orderId];
        }

        try {
            // If not in cache, fetch it
            const response = await fetch(`http://localhost:8080/api/orders/${orderId}`);
            if (!response.ok) {
                throw new Error('Order not found');
            }
            
            const orderData: OrderDetailsWithStatus[] = await response.json();
            
            // Store in cache
            update(orders => ({
                ...orders,
                [orderId]: orderData
            }));

            return orderData;
        } catch (error) {
            // Store null in cache to indicate failed fetch
            update(orders => ({
                ...orders,
                [orderId]: null
            }));
            throw error;
        }
    },

    // Clear the store
    clear() {
        set({});
    }
};
