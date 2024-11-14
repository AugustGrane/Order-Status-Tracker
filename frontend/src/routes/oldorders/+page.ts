import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
        const response = await fetch('/api/get-all-orders');
        const orders = await response.json();

        // Filter orders where all items are at their last step
        const filteredOrders = orders.filter((order: any) =>
            order.items.every((item: any) =>
                item.currentStepIndex === item.differentSteps.length - 1
            )
        );

        return { orders: filteredOrders };
    } catch (e) {
        console.error('Error loading orders:', e);
        return {
            orders: null,
            error: e instanceof Error ? e.message : 'An error occurred loading orders'
        };
    }
}) satisfies PageLoad;
