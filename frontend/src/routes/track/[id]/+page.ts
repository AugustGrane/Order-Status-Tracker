import type { PageLoad } from './$types';
import type { OrderDetailsWithStatus } from '$lib/types';

export const load = (async ({ fetch, params }) => {
    try {
        const response = await fetch(`/api/orders/${params.id}`);
        if (!response.ok) {
            return { 
                orderModel: null,
                orderNotFound: true,
                orderId: params.id
            };
        }
        const orderDetails: OrderDetailsWithStatus[] = await response.json();
        if (!orderDetails || orderDetails.length === 0) {
            return { 
                orderModel: null,
                orderNotFound: true,
                orderId: params.id
            };
        }
        return { 
            orderModel: orderDetails,
            orderNotFound: false,
            orderId: params.id
        };
    } catch (e) {
        console.error('Error loading order:', e);
        return { 
            orderModel: null,
            orderNotFound: true,
            orderId: params.id
        };
    }
}) satisfies PageLoad;
