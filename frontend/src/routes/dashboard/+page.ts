import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
        const response = await fetch('/api/orders/summaries');
        const orders = await response.json();
        
        // Pre-fetch first few order details
        const detailsPromises = orders.slice(0, 5).map(async (order: any) => {
            const detailsResponse = await fetch(`/api/orders/${order.orderId}/details`);
            return detailsResponse.json();
        });
        
        const initialDetails = await Promise.all(detailsPromises);
        
        return { 
            orders,
            initialDetails: Object.fromEntries(
                initialDetails.map((detail, index) => [orders[index].orderId, detail])
            )
        };
    } catch (e) {
        console.error('Error loading orders:', e);
        return {
            orders: [],
            initialDetails: {},
            error: e instanceof Error ? e.message : 'An error occurred loading orders'
        };
    }
}) satisfies PageLoad;