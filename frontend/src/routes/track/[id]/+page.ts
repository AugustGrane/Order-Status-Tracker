import type { PageLoad } from './$types';
import type { OrderDetailsWithStatus } from '$lib/types';

export const load: PageLoad = async ({ params, fetch }) => {
    try {
        const response = await fetch(`http://localhost:8080/api/orders/${params.id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error('Failed to retrieve order data');
        }
        
        const data: OrderDetailsWithStatus[] = await response.json();
        return {
            orderData: data
        };
    } catch (error) {
        return {
            orderData: null,
            error: 'Failed to load order data'
        };
    }
};
