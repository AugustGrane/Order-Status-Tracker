import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
        const response = await fetch('/api/orders/dashboard');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const orders = await response.json();
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