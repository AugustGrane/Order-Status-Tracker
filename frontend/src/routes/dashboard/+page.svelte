<script lang="ts">
    import type { PageData } from './$types';
    import { portal } from './portal';
    import { fade } from 'svelte/transition';
    import StatisticsGrid from '$lib/components/StatisticsGrid.svelte';
    import SearchAndFilter from '$lib/components/SearchAndFilter.svelte';
    import OrderList from '$lib/components/dashboard/OrderList.svelte';

    export let data: PageData;
    const orders = data.orders;

    let expandedOrder: number | null = null;
    let searchQuery = '';
    let sortField = 'date';
    let sortDirection: 'asc' | 'desc' = 'desc';
    let statusFilter = 'active'; // Default to active orders
    let activeTooltip: { orderId: number, rect: DOMRect, type: 'status' | 'notes' } | null = null;
    let tooltipTimeout: number;

    function showTooltip(event: MouseEvent, orderId: number, type: 'status' | 'notes' = 'status') {
        clearTimeout(tooltipTimeout);
        const rect = (event.currentTarget as HTMLElement).getBoundingClientRect();
        activeTooltip = { orderId, rect, type };
        event.stopPropagation();
    }

    function hideTooltip() {
        tooltipTimeout = window.setTimeout(() => {
            activeTooltip = null;
        }, 100); // Small delay to prevent flickering
    }

    function keepTooltip() {
        clearTimeout(tooltipTimeout);
    }

    function isOrderCompleted(order) {
        return order.items.every(item =>
            item.currentStepIndex === item.differentSteps.length - 1
        );
    }

    // Calculate statistics
    $: totalOrders = orders ? orders.length : 0;
    $: activeOrders = orders ? orders.filter(order => !isOrderCompleted(order)).length : 0;
    $: completedOrders = orders ? orders.filter(order => isOrderCompleted(order)).length : 0;
    $: priorityOrders = orders ? orders.filter(order => order.priority && !isOrderCompleted(order)).length : 0;
    $: ordersCompletedToday = orders ? orders.filter(order => {
        const today = new Date().toDateString();
        return isOrderCompleted(order) && new Date(order.orderCreated).toDateString() === today;
    }).length : 0;

    $: averageProcessingTime = orders ? calculateAverageProcessingTime(orders) : 0;

    $: productTypeStats = orders ? calculateProductTypeStats(orders) : [];

    function calculateAverageProcessingTime(orders) {
        const completedOrders = orders.filter(order => isOrderCompleted(order));

        if (completedOrders.length === 0) return 0;

        const totalTime = completedOrders.reduce((sum, order) => {
            const created = new Date(order.orderCreated);
            const now = new Date();
            return sum + (now.getTime() - created.getTime());
        }, 0);

        return Math.round(totalTime / completedOrders.length / (1000 * 60 * 60 * 24)); // Convert to days
    }

    function calculateProductTypeStats(orders) {
        const typeCount = {};
        orders.forEach(order => {
            order.items.forEach(item => {
                const type = item.product_type;
                typeCount[type] = (typeCount[type] || 0) + 1;
            });
        });

        return Object.entries(typeCount)
            .sort(([,a], [,b]) => b - a)
            .slice(0, 3)
            .map(([type, count]) => ({ type, count }));
    }

    $: filteredOrders = orders
        ? orders.filter(order => {
            // First apply status filter
            const statusMatch = statusFilter === 'all' ? true :
                              statusFilter === 'completed' ? isOrderCompleted(order) :
                              !isOrderCompleted(order); // active orders

            // Then apply search filter if status matches
            return statusMatch && (
                order.customerName.toLowerCase().includes(searchQuery.toLowerCase()) ||
                order.orderId.toString().includes(searchQuery) ||
                order.items.some(item => item.item.name.toLowerCase().includes(searchQuery.toLowerCase()))
            );
        })
        : [];

    $: sortedOrders = [...filteredOrders].sort((a, b) => {
        const direction = sortDirection === 'asc' ? 1 : -1;

        switch(sortField) {
            case 'orderId':
                return direction * (a.orderId - b.orderId);
            case 'date':
                return direction * (new Date(a.orderCreated).getTime() - new Date(b.orderCreated).getTime());
            case 'customerName':
                return direction * a.customerName.localeCompare(b.customerName);
            case 'status':
                const aStatus = isOrderCompleted(a);
                const bStatus = isOrderCompleted(b);
                return direction * (aStatus === bStatus ? 0 : aStatus ? 1 : -1);
            case 'priority':
                const aPriority = a.priority ? 1 : 0;
                const bPriority = b.priority ? 1 : 0;
                return direction * (bPriority - aPriority);
            default:
                return 0;
        }
    });
</script>

<main>
    <div class="background">
        <div class="navbar">
            <div class="logo"></div>
            <h1 class="title">Dashboard</h1>
        </div>

        <div class="container">
            <StatisticsGrid
                {priorityOrders}
                {activeOrders}
                {averageProcessingTime}
                {totalOrders}
            />
            <!--{ordersCompletedToday}-->
<!--                {completedOrders}-->
<!--                {productTypeStats}-->
<!--            />-->

            <SearchAndFilter
                bind:searchQuery
                bind:statusFilter
            />

            <OrderList
                orders={sortedOrders}
                bind:expandedOrder
                bind:sortField
                bind:sortDirection
                {showTooltip}
                {hideTooltip}
                {isOrderCompleted}
            />
        </div>
    </div>
</main>

{#if activeTooltip}
    <div use:portal>
        <div class="tooltip-overlay" 
             style="position: fixed; top: {activeTooltip.rect.top + activeTooltip.rect.height}px; left: {activeTooltip.rect.left}px;"
             transition:fade
             on:mouseenter={keepTooltip}
             on:mouseleave={hideTooltip}>
            <div class="tooltip-content">
                {#if activeTooltip.type === 'status' && orders}
                    {#each orders.find(o => o.orderId === activeTooltip.orderId)?.items || [] as item}
                        <div class="tooltip-item">
                            <span class="item-name">{item.item.name}</span>
                            <span class="current-step">{item.differentSteps[item.currentStepIndex].name}</span>
                        </div>
                    {/each}
                {:else if activeTooltip.type === 'notes' && orders}
                    <div class="notes-content">
                        {orders.find(o => o.orderId === activeTooltip.orderId)?.notes || ''}
                    </div>
                {/if}
            </div>
        </div>
    </div>
{/if}

<style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

    :global(body) {
        margin: 0;
        font-family: 'Inter', sans-serif;
        background-color: #f1f5f9;
        color: #1e293b;
    }

    .background {
        min-height: 100vh;
        padding: 2rem;
    }

    .navbar {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 2rem;
    }

    .title {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 600;
        color: #0f172a;
    }

    .container {
        max-width: 1400px;
        margin: 0 auto;
    }

    .tooltip-overlay {
        z-index: 1000;
        margin-top: 0.5rem;
    }

    .tooltip-content {
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        padding: 0.75rem;
        min-width: 200px;
        max-width: 400px;
    }

    .tooltip-item {
        display: flex;
        justify-content: space-between;
        gap: 1rem;
        padding: 0.5rem 0;
    }

    .tooltip-item:not(:last-child) {
        border-bottom: 1px solid #e2e8f0;
    }

    .item-name {
        font-weight: 500;
    }

    .current-step {
        color: #64748b;
        font-size: 0.875rem;
    }

    .notes-content {
        white-space: pre-wrap;
        color: #334155;
        font-size: 0.875rem;
        line-height: 1.5;
    }
</style>