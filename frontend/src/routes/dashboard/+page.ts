import type { PageLoad } from './$types';

export const load = (async ({ fetch }) => {
    try {
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
            initialDetails: {}
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

export const ssr = false;