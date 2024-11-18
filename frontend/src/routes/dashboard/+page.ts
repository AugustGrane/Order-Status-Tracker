import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
        const response = await fetch('/api/orders/dashboard');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        let orders = await response.json();

        // Filter out items that have isDeleted set to true within each order
        orders = orders.map((order: { items: any[]; }) => ({
            ...order,
            items: order.items.filter(item => !item.item.deleted)
        }));

        console.log('Fetched orders:', orders); // Debug log
        return { orders };
    } catch (e) {
        console.error('Error loading orders:', e);
        return {
            orders: [],
            error: e instanceof Error ? e.message : 'An error occurred loading orders'
        };
    }
}) satisfies PageLoad;