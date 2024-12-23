<script lang="ts">
    import { slide } from 'svelte/transition';
    import { quintOut } from 'svelte/easing';
    import { fade } from 'svelte/transition';
    import OrderDetails from './OrderDetails.svelte';
    import DeleteOrder from "./DeleteOrder.svelte";

    export let orders: any[];
    export let expandedOrder: number | null;
    export let showTooltip: (event: MouseEvent, orderId: number, type?: 'status' | 'notes') => void;
    export let hideTooltip: () => void;
    export let isOrderCompleted: (order: any) => boolean;
    export let sortField: string;
    export let sortDirection: 'asc' | 'desc';

    function toggleOrder(orderId: number) {
        expandedOrder = expandedOrder === orderId ? null : orderId;
    }

    function handleSort(field: string) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'desc'; // Default to descending when changing fields
        }
    }

    function getSortIcon(field: string): string {
        if (sortField !== field) return '↕';
        return sortDirection === 'desc' ? '↓' : '↑';
    }
</script>

<div class="order-overview">
    <div class="name-bar">
        <div class="cell sortable" on:click={() => handleSort('orderId')}>
            Ordrenummer
            <span class="sort-icon">{getSortIcon('orderId')}</span>
        </div>
        <div class="cell sortable" on:click={() => handleSort('date')}>
            Dato
            <span class="sort-icon">{getSortIcon('date')}</span>
        </div>
        <div class="cell sortable" on:click={() => handleSort('customerName')}>
            Kundens navn
            <span class="sort-icon">{getSortIcon('customerName')}</span>
        </div>
        <div class="cell sortable" on:click={() => handleSort('status')}>
            Status
            <span class="sort-icon">{getSortIcon('status')}</span>
        </div>
        <div class="cell">Noter</div>
        <div class="cell sortable" on:click={() => handleSort('priority')}>
            Prioritet
            <span class="sort-icon">{getSortIcon('priority')}</span>
        </div>
    </div>
    {#each orders as order}
        <div class="order" class:focus={expandedOrder === order.orderId}>
            <button class="order-header" href="#" on:click={(e) => {
                e.preventDefault();
                toggleOrder(order.orderId);
            }}>
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
                    <div class="content" style="justify-content: center">
                        <span class="status-indicator" 
                              class:completed={isOrderCompleted(order)}
                              on:mouseenter={(e) => showTooltip(e, order.orderId, 'status')}
                              on:mouseleave={hideTooltip}>
                            {isOrderCompleted(order) ? 'Afsluttet' : 'Under behandling'}
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
                    <div class="content" style="justify-content: center">
                        {#if order.priority}
                            <span class="priority-tag">Vigtig</span>
                        {/if}
                    </div>
                </div>
                <div class="cell">
                    <div class="content" style="justify-content: center">
                        <DeleteOrder orderId={order.orderId} />
                    </div>
                </div>
            </button>

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
        grid-template-columns: minmax(120px, 1fr) minmax(120px, 1fr) minmax(200px, 2fr) minmax(140px, 1fr) minmax(140px, 1fr) minmax(100px, 0.6fr) minmax(30px, 0.35fr);
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
        grid-template-columns: minmax(120px, 1fr) minmax(120px, 1fr) minmax(200px, 2fr) minmax(140px, 1fr) minmax(140px, 1fr) minmax(100px, 0.6fr) minmax(30px, 0.35fr);
        cursor: pointer;
        transition: background-color 0.2s;
        background: none;
        border: none;
        padding: 0;
        text-align: left;
        width: 100%;
    }

    .order-header:hover {
        background-color: #f8fafc;
    }

    .focus {
        border: 0.5px solid #015ecc;
    }

    .cell {
        padding: 1rem;
        height: 3.5rem;
        display: flex;
        align-items: center;
        position: relative;
        overflow: hidden;
    }

    .sortable {
        cursor: pointer;
        user-select: none;
        transition: color 0.2s;
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .sortable:hover {
        color: #1e293b;
    }

    .sort-icon {
        font-size: 0.875rem;
        opacity: 0.5;
        transition: opacity 0.2s;
    }

    .sortable:hover .sort-icon {
        opacity: 1;
    }

    .content {
        width: 100%;
        display: flex;
        align-items: center;
        min-width: 0;
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

    @media (max-width: 1300px) {
        .name-bar, .order-header {
            grid-template-columns: minmax(130px, 1fr) minmax(100px, 1fr) minmax(140px, 1.5fr) minmax(120px, 1fr) minmax(120px, 1fr) minmax(90px, 0.6fr) minmax(30px, 0.35fr);
        }
        .cell {
            padding: 0.50rem;
        }
        .content {
            font-size: 0.875rem;
        }
        .status-indicator, .priority-tag {
            padding: 0.25rem 0.5rem;
            font-size: 0.625rem;
        }
    }
</style>
