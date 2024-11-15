<script lang="ts">
    import type { PageData } from './$types';
    import { slide } from 'svelte/transition';
    import { quintOut } from 'svelte/easing';
    import { fade } from 'svelte/transition';
    import { onMount } from 'svelte';
    import { portal } from './portal';
    
    export let data: PageData;
    const orders = data.orders;
    
    let expandedOrder: number | null = null;
    let searchQuery = '';
    let sortOption = 'newest';
    let statusFilter = 'active'; // Default to active orders
    let activeTooltip: { orderId: number, rect: DOMRect } | null = null;

    function showTooltip(event: MouseEvent, orderId: number) {
        const rect = (event.currentTarget as HTMLElement).getBoundingClientRect();
        activeTooltip = { orderId, rect };
        event.stopPropagation();
    }

    function hideTooltip() {
        activeTooltip = null;
    }

    function isOrderCompleted(order) {
        return order.items.every(item => 
            item.currentStepIndex === item.differentSteps.length - 1 //&&
            //item.differentSteps[item.differentSteps.length - 1].name.toLowerCase() === "product sent"
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
        switch(sortOption) {
            case 'oldest':
                return new Date(a.orderCreated).getTime() - new Date(b.orderCreated).getTime();
            case 'process':
                return b.items.length - a.items.length;
            default: // newest
                return new Date(b.orderCreated).getTime() - new Date(a.orderCreated).getTime();
        }
    });
    
    function toggleOrder(orderId: number) {
        expandedOrder = expandedOrder === orderId ? null : orderId;
    }
</script>

<main>
    <div class="background">
        <div class="navbar">
            <div class="logo"></div>
            <h1 class="title">Dashboard</h1>
        </div>
        
        <div class="container">
            <!-- Statistics Section -->
            <div class="statistics-grid">
                <div class="stat-card">
                    <div class="stat-icon">📊</div>
                    <div class="stat-content">
                        <div class="stat-value">{activeOrders}</div>
                        <div class="stat-label">Aktive ordre</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">⚡</div>
                    <div class="stat-content">
                        <div class="stat-value">{priorityOrders}</div>
                        <div class="stat-label">Prioritets ordre</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">✅</div>
                    <div class="stat-content">
                        <div class="stat-value">{completedOrders}</div>
                        <div class="stat-label">Færdige ordre</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">⏱️</div>
                    <div class="stat-content">
                        <div class="stat-value">{averageProcessingTime} dage</div>
                        <div class="stat-label">Gns. behandlingstid</div>
                    </div>
                </div>
                <div class="stat-card product-types">
                    <div class="stat-icon">📦</div>
                    <div class="stat-content">
                        <div class="stat-label">Top produkttyper</div>
                        <div class="product-type-list">
                            {#each productTypeStats as {type, count}}
                                <div class="product-type-item">
                                    <span class="type-name">{type}</span>
                                    <span class="type-count">{count}</span>
                                </div>
                            {/each}
                        </div>
                    </div>
                </div>
            </div>

            <div class="search-filter">
                <div class="left">
                    <h2 class="active-orders-text">
                        {#if statusFilter === 'active'}
                            Aktive ordre
                        {:else if statusFilter === 'completed'}
                            Færdige ordre
                        {:else}
                            Alle ordre
                        {/if}
                    </h2>
                    <p class="active-orders">Se og opdater kundeordre</p>
                    <div class="search">
                        <input 
                            class="search-field" 
                            bind:value={searchQuery}
                            placeholder="Søg efter navn, ordrenummer eller artikler"
                        >
                    </div>
                </div>
                <div class="right">
                    <div class="filter-group">
                        <select class="status-filter" bind:value={statusFilter}>
                            <option value="active">Aktive</option>
                            <option value="completed">Færdige</option>
                            <option value="all">Alle</option>
                        </select>
                        <select class="dropdown-filter" bind:value={sortOption}>
                            <option value="newest">Nyeste</option>
                            <option value="oldest">Ældste</option>
                            <option value="process">Længst i process</option>
                        </select>
                    </div>
                </div>
            </div>

            <div class="order-overview">
                <div class="name-bar">
                    <div class="order-number">Ordrenummer</div>
                    <div class="date">Dato</div>
                    <div class="customer">Kundens navn</div>
                    <div class="items-count">Antal artikler</div>
                    <div class="notes-column">Noter</div>
                    <div class="status">Status</div>
                </div>

                {#each sortedOrders as order}
                    <div class="order">
                        <div class="order-header" on:click={() => toggleOrder(order.orderId)}>
                            <div class="order-number">{order.orderId}</div>
                            <div class="date">{new Date(order.orderCreated).toLocaleDateString('da-DK')}</div>
                            <div class="customer">{order.customerName}</div>
                            <div class="items-count">{order.items.length}</div>
                            <div class="notes-column">
                                {#if order.notes}
                                    <div class="notes-preview"
                                         on:click={(e) => showTooltip(e, order.orderId)}
                                         on:mouseenter={(e) => showTooltip(e, order.orderId)}
                                         on:mouseleave={hideTooltip}>
                                        <span class="notes-icon">✏️</span>
                                        <span class="notes-text">{order.notes}</span>
                                    </div>
                                {:else}
                                    <span class="no-notes">-</span>
                                {/if}
                            </div>
                            <div class="status">
                                <div class="status-indicator" style="background-color: {
                                    order.items.every(item => 
                                        item.currentStepIndex === item.differentSteps.length - 1 //&&
                                        //item.differentSteps[item.differentSteps.length - 1].name.toLowerCase() === "product sent"
                                    ) ? '#4CAF50' : '#FFA726'
                                }">
                                    {order.items.every(item => 
                                        item.currentStepIndex === item.differentSteps.length - 1 //&&
                                        //item.differentSteps[item.differentSteps.length - 1].name.toLowerCase() === "product sent"
                                    ) ? 'Færdig' : 'I proces'}
                                </div>
                            </div>
                        </div>

                        {#if expandedOrder === order.orderId}
                            <div class="order-details" transition:slide={{duration: 300, easing: quintOut}}>
                                {#each order.items as item}
                                    <div class="item-details">
                                        <div class="item-info">
                                            <span class="item-name">{item.item.name}</span>
                                            <span class="item-amount">Antal: {item.itemAmount}</span>
                                        </div>
                                        <div class="item-progress">
                                            <div class="progress-steps">
                                                {#each item.differentSteps as step, index}
                                                    <div class="step" class:completed={index <= item.currentStepIndex}>
                                                        <div class="step-content">
                                                            <div class="step-marker"></div>
                                                            <div class="step-name">{step.name}</div>
                                                        </div>
                                                        {#if index < item.differentSteps.length - 1}
                                                            <div class="step-line" class:completed={index < item.currentStepIndex}></div>
                                                        {/if}
                                                    </div>
                                                {/each}
                                            </div>
                                            <select 
                                                class="status-select"
                                                value={item.currentStepIndex}
                                            >
                                                {#each item.differentSteps as step, index}
                                                    <option value={index}>{step.name}</option>
                                                {/each}
                                            </select>
                                        </div>
                                    </div>
                                {/each}
                            </div>
                        {/if}
                    </div>
                {/each}
            </div>
        </div>
    </div>
</main>

{#if activeTooltip}
    <div use:portal>
        <div class="tooltip-overlay" 
             style="left: {activeTooltip.rect.left}px; 
                    top: {activeTooltip.rect.bottom + window.scrollY + 4}px;">
            <div class="tooltip" 
                 transition:fade={{duration: 100}}
                 on:mouseenter={() => activeTooltip = activeTooltip}
                 on:mouseleave={hideTooltip}>
                {orders.find(order => order.orderId === activeTooltip.orderId)?.notes}
            </div>
        </div>
    </div>
{/if}

<style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

    :global(body) {
        margin: 0;
        font-family: 'Inter', sans-serif;
        background-color: #f5f5f5;
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
        font-size: 2rem;
        font-weight: 600;
        color: #1a1a1a;
        margin: 0;
    }

    .container {
        max-width: 1200px;
        margin: 0 auto;
    }

    .search-filter {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        margin-bottom: 2rem;
    }

    .active-orders-text {
        font-size: 1.5rem;
        font-weight: 600;
        margin: 0 0 0.5rem 0;
    }

    .active-orders {
        color: #666;
        margin: 0 0 1rem 0;
    }

    .search-field {
        width: 400px;
        padding: 0.75rem 1rem;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        font-size: 1rem;
        transition: all 0.2s;
    }

    .search-field:focus {
        outline: none;
        border-color: #2196F3;
        box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
    }

    .dropdown-filter {
        padding: 0.75rem 1rem;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        font-size: 1rem;
        background-color: white;
        cursor: pointer;
    }

    .order-overview {
        background: white;
        border-radius: 12px;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }

    .name-bar {
        display: grid;
        grid-template-columns: 0.8fr 0.8fr 1fr 0.6fr 1.2fr 0.8fr;
        gap: 1rem;
        padding: 1rem;
        background-color: #f8f9fa;
        border-radius: 8px 8px 0 0;
        font-weight: 600;
        color: #4a5568;
    }

    .order-header {
        display: grid;
        grid-template-columns: 0.8fr 0.8fr 1fr 0.6fr 1.2fr 0.8fr;
        gap: 1rem;
        padding: 1rem;
        background-color: white;
        cursor: pointer;
        transition: background-color 0.2s;
        align-items: center;
        position: relative;
        z-index: 1;
    }

    .order-header > div {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .status-indicator {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 1rem;
        color: white;
        font-size: 0.875rem;
        font-weight: 500;
    }

    .order-details {
        padding: 1rem;
        background: #f8f9fa;
        border-top: 1px solid #e9ecef;
    }

    .item-details {
        background: white;
        border-radius: 8px;
        padding: 1rem;
        margin-bottom: 1rem;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    }

    .item-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .item-name {
        font-weight: 600;
        font-size: 1.1rem;
    }

    .item-amount {
        color: #666;
    }

    .progress-steps {
        display: flex;
        align-items: center;
        margin: 2rem 0;
        padding: 0 1rem;
    }

    .step {
        display: flex;
        align-items: center;
        flex: 1;
    }

    .step-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
        z-index: 2;
    }

    .step-marker {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background-color: #e0e0e0;
        border: 3px solid #fff;
        box-shadow: 0 0 0 2px #e0e0e0;
        transition: all 0.3s ease;
    }

    .step.completed .step-marker {
        background-color: #4CAF50;
        box-shadow: 0 0 0 2px #4CAF50;
    }

    .step-line {
        flex: 1;
        height: 2px;
        background-color: #e0e0e0;
        margin: 0 8px;
        position: relative;
        top: -12px;
        transition: all 0.3s ease;
    }

    .step-line.completed {
        background-color: #4CAF50;
    }

    .step-name {
        font-size: 0.875rem;
        color: #666;
        margin-top: 0.5rem;
        text-align: center;
        max-width: 120px;
        white-space: normal;
    }

    .step.completed .step-name {
        color: #4CAF50;
    }

    .status-select {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #e0e0e0;
        border-radius: 4px;
        margin-top: 2rem;
        background-color: white;
    }

    .statistics-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        margin-bottom: 2rem;
    }

    .stat-card {
        background: white;
        border-radius: 12px;
        padding: 1.5rem;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: flex-start;
        gap: 1rem;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }

    .stat-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 2px 4px rgba(0, 0, 0, 0.06);
    }

    .stat-icon {
        font-size: 1.5rem;
        background: #f8f9fa;
        padding: 0.75rem;
        border-radius: 8px;
    }

    .stat-content {
        flex: 1;
    }

    .stat-value {
        font-size: 1.5rem;
        font-weight: 600;
        color: #2196F3;
        margin-bottom: 0.25rem;
    }

    .stat-label {
        font-size: 0.875rem;
        color: #666;
        font-weight: 500;
    }

    .product-types {
        grid-column: span 1;
    }

    .product-type-list {
        margin-top: 0.5rem;
    }

    .product-type-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.25rem 0;
        font-size: 0.875rem;
    }

    .type-name {
        color: #333;
    }

    .type-count {
        background: #e3f2fd;
        color: #2196F3;
        padding: 0.125rem 0.5rem;
        border-radius: 12px;
        font-weight: 500;
    }

    .filter-group {
        display: flex;
        gap: 1rem;
    }

    .status-filter,
    .dropdown-filter {
        padding: 0.75rem 1rem;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        font-size: 1rem;
        background-color: white;
        cursor: pointer;
        min-width: 120px;
    }

    .status-filter:focus,
    .dropdown-filter:focus {
        outline: none;
        border-color: #2196F3;
        box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
    }

    .notes-column {
        font-size: 0.9rem;
        color: #4a5568;
        min-width: 0; /* Enable text truncation */
    }

    .notes-preview {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 4px;
        transition: background-color 0.2s;
        overflow: visible;
        position: relative;
    }

    .notes-preview:hover {
        background-color: #f1f5f9;
    }

    .notes-icon {
        flex-shrink: 0; /* Prevent emoji from shrinking */
        font-size: 0.9rem;
    }

    .notes-text {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        min-width: 0; /* Enable truncation */
    }

    .no-notes {
        color: #a0aec0;
    }

    .tooltip-overlay {
        position: fixed;
        z-index: 9999;
        pointer-events: none;
    }

    .tooltip {
        position: absolute;
        max-width: 400px;
        padding: 0.75rem 1rem;
        background-color: #334155;
        color: white;
        border-radius: 6px;
        font-size: 0.9rem;
        line-height: 1.5;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        word-wrap: break-word;
        white-space: pre-wrap;
        pointer-events: auto;
    }

    .tooltip::before {
        content: '';
        position: absolute;
        top: -4px;
        left: 16px;
        width: 8px;
        height: 8px;
        background-color: #334155;
        transform: rotate(45deg);
    }

    .order {
        position: relative;
    }
</style>