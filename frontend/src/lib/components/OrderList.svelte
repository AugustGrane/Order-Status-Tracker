<script lang="ts">
    import { slide } from 'svelte/transition';
    import { quintOut } from 'svelte/easing';
    import { fade } from 'svelte/transition';
    import OrderDetails from './OrderDetails.svelte';

    export let orders: any[];
    export let expandedOrder: number | null;
    export let showTooltip: (event: MouseEvent, orderId: number, type?: 'status' | 'notes') => void;
    export let hideTooltip: () => void;
    export let isOrderCompleted: (order: any) => boolean;

    function toggleOrder(orderId: number) {
        expandedOrder = expandedOrder === orderId ? null : orderId;
    }
</script>

<div class="order-overview">
    <div class="name-bar">
        <div class="cell">Ordrenummer</div>
        <div class="cell">Dato</div>
        <div class="cell">Kundens navn</div>
        <div class="cell">Status</div>
        <div class="cell">Noter</div>
        <div class="cell">Prioritet</div>
    </div>

    {#each orders as order}
        <div class="order">
            <div class="order-header" on:click={() => toggleOrder(order.orderId)}>
                <div class="cell">
                    <span class="content">{order.orderId}</span>
                </div>
                <div class="cell">
                    <span class="content">{new Date(order.orderCreated).toLocaleDateString('da-DK')}</span>
                </div>
                <div class="cell">
                    <span class="content customer-name">{order.customerName}</span>
                </div>
                <div class="cell">
                    <div class="content">
                        <span class="status-indicator" 
                              class:completed={isOrderCompleted(order)}
                              on:mouseenter={(e) => showTooltip(e, order.orderId, 'status')}
                              on:mouseleave={hideTooltip}>
                            {isOrderCompleted(order) ? 'Completed' : 'In Progress'}
                        </span>
                    </div>
                </div>
                <div class="cell">
                    <div class="content">
                        {#if order.notes}
                            <div class="notes-preview"
                                 on:mouseenter={(e) => showTooltip(e, order.orderId, 'notes')}
                                 on:mouseleave={hideTooltip}>
                                <span class="notes-icon">📝</span>
                                <span class="notes-text">{order.notes}</span>
                            </div>
                        {:else}
                            <span class="no-notes">-</span>
                        {/if}
                    </div>
                </div>
                <div class="cell">
                    <div class="content">
                        {#if order.priority}
                            <span class="priority-tag">High</span>
                        {/if}
                    </div>
                </div>
            </div>

            {#if expandedOrder === order.orderId}
                <div class="order-details" transition:slide={{duration: 300, easing: quintOut}}>
                    <OrderDetails {order} />
                </div>
            {/if}
        </div>
    {/each}
</div>

<style>
    .order-overview {
        background: white;
        border-radius: 12px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        overflow: hidden;
    }

    .name-bar {
        display: grid;
        grid-template-columns: minmax(120px, 1fr) minmax(120px, 1fr) minmax(200px, 2fr) minmax(140px, 1fr) minmax(140px, 1fr) minmax(120px, 1fr);
        background: #f8fafc;
        font-weight: 600;
        font-size: 0.9rem;
        color: #64748b;
        border-bottom: 1px solid #e2e8f0;
    }

    .order {
        border-bottom: 1px solid #e2e8f0;
    }

    .order:last-child {
        border-bottom: none;
    }

    .order-header {
        display: grid;
        grid-template-columns: minmax(120px, 1fr) minmax(120px, 1fr) minmax(200px, 2fr) minmax(140px, 1fr) minmax(140px, 1fr) minmax(120px, 1fr);
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .order-header:hover {
        background-color: #f8fafc;
    }

    .cell {
        padding: 1rem;
        height: 3.5rem;
        display: flex;
        align-items: center;
        position: relative;
        overflow: hidden;
    }

    .content {
        width: 100%;
        display: flex;
        align-items: center;
        min-width: 0; /* This ensures proper text truncation */
    }

    .customer-name {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .status-indicator {
        display: inline-flex;
        align-items: center;
        padding: 0.25rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.875rem;
        font-weight: 500;
        background-color: #fef3c7;
        color: #92400e;
        cursor: help;
        height: 1.5rem;
    }

    .status-indicator.completed {
        background-color: #dcfce7;
        color: #166534;
    }

    .priority-tag {
        display: inline-flex;
        align-items: center;
        padding: 0.25rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.875rem;
        font-weight: 500;
        background-color: #fee2e2;
        color: #991b1b;
        height: 1.5rem;
    }

    .order-details {
        padding: 1rem;
        background-color: #f8fafc;
        border-top: 1px solid #e2e8f0;
    }

    .notes-preview {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        max-width: 200px;
        cursor: help;
        height: 1.5rem;
    }

    .notes-icon {
        flex-shrink: 0;
    }

    .notes-text {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        color: #64748b;
    }

    .no-notes {
        color: #94a3b8;
        height: 1.5rem;
        display: flex;
        align-items: center;
    }
</style>
