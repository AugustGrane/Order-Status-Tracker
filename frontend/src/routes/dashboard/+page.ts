import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
        const response = await fetch('/api/get-all-orders'); // Note: removed http://localhost:8080
        const orders = await response.json();
        return { orders };
    } catch (e) {
        console.error('Error loading orders:', e);
        return {
            orders: null,
            error: e instanceof Error ? e.message : 'An error occurred loading orders'
        };
    }
}) satisfies PageLoad;