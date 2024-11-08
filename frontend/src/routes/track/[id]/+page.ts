import type { PageLoad } from './$types';

export const load = (async ({ fetch, params }) => {
    try {
        const response = await fetch(`/api/orders/${params.id}`);
        if (!response.ok) {
            return { 
                order: null,
                orderNotFound: true,
                orderId: params.id
            };
        }
        const order = await response.json();
        if (!order) {
            return { 
                order: null,
                orderNotFound: true,
                orderId: params.id
            };
        }
        return { 
            order,
            orderNotFound: false,
            orderId: params.id
        };
    } catch (e) {
        return { 
            order: null,
            orderNotFound: true,
            orderId: params.id
        };
    }
}) satisfies PageLoad;
