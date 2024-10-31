import type { PageLoad } from './$types';
import { orderStore } from '$lib/stores/orderStore';
import { error } from '@sveltejs/kit';

export const load = (async ({ params }) => {
    try {
        orderStore.clear()
        const order = await orderStore.getOrder(params.id);
        if (!order) {
            throw error(404, 'Order not found');
        }
        return { order };
    } catch (e) {
        throw error(404, 'Order not found');
    }
}) satisfies PageLoad;
