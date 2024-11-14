import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
        // Fetch order summaries first
        const response = await fetch('/api/orders/summaries', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const orders = await response.json();
        
        return {
            orders,
            initialDetails: {}  // Initial details will be loaded in the background
        };
    } catch (error) {
        console.error('Error loading orders:', error);
        return {
            orders: [],
            initialDetails: {}
        };
    }
}) satisfies PageLoad;

export const ssr = false;