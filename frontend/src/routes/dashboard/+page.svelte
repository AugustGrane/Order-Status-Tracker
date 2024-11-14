<script lang="ts">
    import { onMount } from 'svelte';
    import { slide } from 'svelte/transition';
    import { quintOut } from 'svelte/easing';
    import type { OrderSummary, OrderDetailsWithStatus, SortDirection, SortableFields, DashboardState } from '$lib/types';

    let orders: OrderSummary[] = [];
    let filteredOrders: OrderSummary[] = [];
    let orderDetails: Record<number, OrderDetailsWithStatus[]> = {};
    let expandedOrderId: number | null = null;
    let loadingOrderId: number | null = null;
    let loadingQueue: number[] = [];
    let isProcessingQueue = false;
    let sortField: SortableFields = 'orderId';
    let sortDirection: SortDirection = 'asc';
    let searchQuery = '';
    let isRefreshing = false;
    let lastRefresh = new Date();
    let autoRefresh = false;
    let autoRefreshInterval: ReturnType<typeof setInterval>;

    // Summary statistics
    $: totalOrders = orders.length;
    $: priorityOrders = orders.filter(o => o.priority).length;
    $: todayOrders = orders.filter(o => {
        const today = new Date();
        const orderDate = new Date(o.orderCreated);
        return orderDate.toDateString() === today.toDateString();
    }).length;

    // Search and filter functionality
    $: filteredOrders = orders.filter(order => {
        if (!searchQuery) return true;
        const searchLower = searchQuery.toLowerCase();
        return (
            order.orderId.toString().includes(searchLower) ||
            order.customerName.toLowerCase().includes(searchLower)
        );
    });

    // Auto-refresh setup
    onMount(() => {
        loadOrders();
        return () => {
            if (autoRefreshInterval) clearInterval(autoRefreshInterval);
        };
    });

    function toggleAutoRefresh() {
        autoRefresh = !autoRefresh;
        if (autoRefresh) {
            autoRefreshInterval = setInterval(async () => {
                await refreshOrders();
            }, 30000); // Refresh every 30 seconds
        } else {
            clearInterval(autoRefreshInterval);
        }
    }

    async function refreshOrders() {
        if (isRefreshing) return;
        isRefreshing = true;
        try {
            await loadOrders();
            lastRefresh = new Date();
        } finally {
            isRefreshing = false;
        }
    }

    async function loadOrders(): Promise<void> {
        try {
            const response = await fetch('/api/orders/summaries');
            if (!response.ok) throw new Error('Failed to fetch orders');
            orders = await response.json();
            startBackgroundLoading();
        } catch (error) {
            console.error('Error loading orders:', error);
        }
    }

    async function loadOrderDetails(orderId: number): Promise<OrderDetailsWithStatus[] | undefined> {
        if (orderDetails[orderId]) return orderDetails[orderId];
        
        loadingOrderId = orderId;
        try {
            const response = await fetch(`/api/orders/${orderId}`);
            if (!response.ok) throw new Error('Failed to fetch order details');
            const details = await response.json();
            orderDetails = { ...orderDetails, [orderId]: details };
            return details;
        } catch (error) {
            console.error(`Error loading order ${orderId} details:`, error);
            return undefined;
        } finally {
            if (loadingOrderId === orderId) {
                loadingOrderId = null;
            }
        }
    }

    async function processQueue(): Promise<void> {
        if (isProcessingQueue || loadingQueue.length === 0) return;
        
        isProcessingQueue = true;
        while (loadingQueue.length > 0) {
            const orderId = loadingQueue.shift();
            if (orderId !== undefined && !orderDetails[orderId]) {
                await loadOrderDetails(orderId);
                // Add a small delay between requests to not overwhelm the server
                await new Promise(resolve => setTimeout(resolve, 500));
            }
        }
        isProcessingQueue = false;
    }

    function startBackgroundLoading(): void {
        loadingQueue = orders
            .map(order => order.orderId)
            .filter(id => !orderDetails[id]);
        processQueue();
    }

    async function toggleOrderDetails(orderId: number): Promise<void> {
        if (expandedOrderId === orderId) {
            expandedOrderId = null;
        } else {
            expandedOrderId = orderId;
            if (!orderDetails[orderId]) {
                await loadOrderDetails(orderId);
            }
        }
    }

    function handleKeyDown(event: KeyboardEvent, orderId: number): void {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            toggleOrderDetails(orderId);
        }
    }

    async function handleStatusChange(id: number, newStatus: string): Promise<void> {
        try {
            const response = await fetch(`/api/order-product-types/${id}/next-step`, {
                method: 'POST'
            });
            if (!response.ok) throw new Error('Failed to update status');
            if (expandedOrderId !== null) {
                await loadOrderDetails(expandedOrderId);
            }
        } catch (error) {
            console.error('Error updating status:', error);
        }
    }

    function formatDate(dateString: string): string {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('da-DK', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function sortOrders(field: SortableFields): void {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }

        orders = orders.sort((a, b) => {
            let aValue = a[field];
            let bValue = b[field];

            // Handle date comparison
            if (field === 'orderCreated') {
                aValue = new Date(aValue as string).getTime();
                bValue = new Date(bValue as string).getTime();
            }

            // Handle numeric comparison
            if (field === 'orderId' || field === 'totalItems') {
                aValue = Number(aValue);
                bValue = Number(bValue);
            }

            if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1;
            if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1;
            return 0;
        });
        orders = [...orders]; // Trigger reactivity
    };
</script>

<main>
    <div class="dashboard">
        <div class="dashboard-header">
            <div class="logo-container">
                <img src="/gtryk_logo.png" alt="GTryk Logo" class="logo" />
            </div>
        </div>

        <div class="dashboard-stats">
            <div class="stat-card">
                <h3>Totale Ordrer</h3>
                <p>{totalOrders}</p>
            </div>
            <div class="stat-card">
                <h3>Prioritets Ordrer</h3>
                <p>{priorityOrders}</p>
            </div>
            <div class="stat-card">
                <h3>Dagens Ordrer</h3>
                <p>{todayOrders}</p>
            </div>
        </div>

        <div class="table-container">
            <div class="table-header">
                <div class="header-title">
                    <h1>Ordre Oversigt</h1>
                </div>
                <div class="table-controls">
                    <div class="search-container">
                        <input
                            type="text"
                            bind:value={searchQuery}
                            placeholder="Søg efter ordre ID eller kunde..."
                            class="search-input"
                        />
                    </div>
                    <div class="refresh-controls">
                        <button
                            class="refresh-button"
                            on:click={refreshOrders}
                            disabled={isRefreshing}
                        >
                            <span class="material-icons" class:spinning={isRefreshing}>refresh</span>
                        </button>
                        <label class="auto-refresh-label">
                            <input
                                type="checkbox"
                                bind:checked={autoRefresh}
                                on:change={toggleAutoRefresh}
                            />
                            Auto-opdater
                        </label>
                        <span class="last-refresh">
                            Sidst opdateret: {formatDate(lastRefresh.toISOString())}
                        </span>
                    </div>
                </div>
            </div>
            <div class="header">
                <button 
                    class="col header-col" 
                    style="width: 150px;"
                    on:click={() => sortOrders('orderId')}
                    class:active={sortField === 'orderId'}
                >
                    <div class="header-content">
                        <span>Ordrenummer</span>
                        <span class="sort-icon" class:visible={sortField === 'orderId'}>
                            {sortDirection === 'asc' ? '↑' : '↓'}
                        </span>
                    </div>
                </button>
                <button 
                    class="col header-col" 
                    style="width: 150px;"
                    on:click={() => sortOrders('orderCreated')}
                    class:active={sortField === 'orderCreated'}
                >
                    <div class="header-content">
                        <span>Dato</span>
                        <span class="sort-icon" class:visible={sortField === 'orderCreated'}>
                            {sortDirection === 'asc' ? '↑' : '↓'}
                        </span>
                    </div>
                </button>
                <button 
                    class="col header-col" 
                    style="width: 250px;"
                    on:click={() => sortOrders('customerName')}
                    class:active={sortField === 'customerName'}
                >
                    <div class="header-content">
                        <span>Kundens navn</span>
                        <span class="sort-icon" class:visible={sortField === 'customerName'}>
                            {sortDirection === 'asc' ? '↑' : '↓'}
                        </span>
                    </div>
                </button>
                <button 
                    class="col header-col" 
                    style="width: 150px;"
                    on:click={() => sortOrders('totalItems')}
                    class:active={sortField === 'totalItems'}
                >
                    <div class="header-content">
                        <span>Antal artikler</span>
                        <span class="sort-icon" class:visible={sortField === 'totalItems'}>
                            {sortDirection === 'asc' ? '↑' : '↓'}
                        </span>
                    </div>
                </button>
                <button 
                    class="col header-col" 
                    style="width: 100px;"
                    on:click={() => sortOrders('priority')}
                    class:active={sortField === 'priority'}
                >
                    <div class="header-content">
                        <span>Prioritet</span>
                        <span class="sort-icon" class:visible={sortField === 'priority'}>
                            {sortDirection === 'asc' ? '↑' : '↓'}
                        </span>
                    </div>
                </button>
            </div>
            {#each filteredOrders as order}
                <div 
                    class="order-container"
                    class:expanded={expandedOrderId === order.orderId}
                    on:click={() => toggleOrderDetails(order.orderId)}
                    on:keydown={(e) => handleKeyDown(e, order.orderId)}
                    tabindex="0"
                    role="button"
                    aria-expanded={expandedOrderId === order.orderId}
                >
                    <div class="order-summary">
                        <div class="col" style="width: 150px;">#{order.orderId}</div>
                        <div class="col" style="width: 150px;">{formatDate(order.orderCreated)}</div>
                        <div class="col" style="width: 250px;">{order.customerName}</div>
                        <div class="col" style="width: 150px;">{order.totalItems} stk.</div>
                        <div class="col priority-col" style="width: 100px;">
                            <span class="priority-badge" class:high={order.priority}>
                                {#if order.priority}
                                    <svg class="urgent-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M8 2L2 14H14L8 2Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                        <path d="M8 11V6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
                                        <circle cx="8" cy="13" r="0.5" fill="currentColor"/>
                                    </svg>
                                {/if}
                                {order.priority ? 'Haster' : 'Almindelig'}
                            </span>
                        </div>
                        <div class="expand-icon" class:expanded={expandedOrderId === order.orderId}>
                            <svg 
                                class="chevron-icon" 
                                width="24" 
                                height="24" 
                                viewBox="0 0 24 24" 
                                fill="none" 
                                xmlns="http://www.w3.org/2000/svg"
                            >
                                <path 
                                    d="M6 9L12 15L18 9" 
                                    stroke="currentColor" 
                                    stroke-width="2" 
                                    stroke-linecap="round" 
                                    stroke-linejoin="round"
                                />
                            </svg>
                        </div>
                    </div>
                    {#if expandedOrderId === order.orderId}
                        <div 
                            class="details-container" 
                            transition:slide={{duration: 300, easing: quintOut}}
                            on:click|stopPropagation
                            role="region"
                            aria-label="Ordre detaljer"
                        >
                            <div class="item-status-container">
                                {#if loadingOrderId === order.orderId}
                                    <div class="loading">
                                        <div class="loading-spinner"></div>
                                        <span>Indlæser ordre detaljer...</span>
                                    </div>
                                {:else if orderDetails[order.orderId]}
                                    {#each orderDetails[order.orderId] as detail}
                                        <div class="item-status-instance">
                                            <div class="actual-item">
                                                <span class="item-name">{detail.item.name}</span>
                                                <span class="item-amount">{detail.itemAmount} stk.</span>
                                                <span class="product-type">{detail.product_type}</span>
                                            </div>
                                            <div class="actual-status">
                                                <select 
                                                    class="dropdown-status"
                                                    on:click|stopPropagation
                                                    on:change={(e) => handleStatusChange(detail.id, e.target.value)}
                                                    aria-label="Vælg status"
                                                >
                                                    {#each detail.differentSteps as step, index}
                                                        <option 
                                                            value={index} 
                                                            selected={index === detail.currentStepIndex}
                                                        >
                                                            {step.name}
                                                        </option>
                                                    {/each}
                                                </select>
                                                <div class="status-timestamp">
                                                    {#if detail.updated && detail.updated[detail.currentStepIndex]}
                                                        Opdateret: {formatDate(detail.updated[detail.currentStepIndex])}
                                                    {/if}
                                                </div>
                                            </div>
                                        </div>
                                    {/each}
                                {/if}
                            </div>
                        </div>
                    {/if}
                </div>
            {/each}
        </div>
    </div>
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    html {
        font-family: Roboto, Arial, sans-serif;
    }

    .dashboard {
        padding: 20px;
        background-color: #f5f7fa;
        min-height: 100vh;
    }

    .dashboard-header {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 20px;
        padding: 20px 20px 0 20px;
    }

    .logo-container {
        width: 100%;
        display: flex;
        justify-content: center;
    }

    .logo {
        height: 80px;
        width: auto;
        object-fit: contain;
    }

    .dashboard-stats {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        padding: 1rem;
        margin-bottom: 2rem;
    }

    .stat-card {
        background: white;
        padding: 1.5rem;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        text-align: center;
    }

    .stat-card h3 {
        margin: 0;
        font-size: 1rem;
        color: #666;
    }

    .stat-card p {
        margin: 0.5rem 0 0;
        font-size: 2rem;
        font-weight: bold;
        color: #2c3e50;
    }

    .table-container {
        width: 100%;
        max-width: 850px;
        margin: 0 auto;
        overflow: hidden;
        background: white;
        border-radius: 12px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.05);
        padding: 20px;
    }

    .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
    }

    .header-title h1 {
        font-size: 24px;
        font-weight: 600;
        color: #2c3e50;
        margin: 0;
    }

    .table-controls {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
        gap: 1rem;
    }

    .search-container {
        flex: 1;
    }

    .search-input {
        width: 100%;
        padding: 0.5rem 1rem;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 1rem;
    }

    .refresh-controls {
        display: flex;
        align-items: center;
        gap: 1rem;
    }

    .refresh-button {
        background: none;
        border: none;
        cursor: pointer;
        padding: 0.5rem;
        border-radius: 50%;
        transition: background-color 0.2s;
    }

    .refresh-button:hover {
        background-color: #f0f0f0;
    }

    .refresh-button:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }

    .spinning {
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        100% {
            transform: rotate(360deg);
        }
    }

    .auto-refresh-label {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.9rem;
        color: #666;
    }

    .last-refresh {
        font-size: 0.8rem;
        color: #888;
    }

    .header {
        display: flex;
        padding: 12px 16px;
        background-color: #f8f9fa;
        border-radius: 8px;
        margin-bottom: 15px;
        gap: 8px;
    }

    .header-col {
        display: flex;
        align-items: center;
        background: white;
        border: 1px solid #e2e8f0;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
        color: #64748b;
        font-size: 13px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
        padding: 8px 12px;
        text-align: left;
        transition: all 0.2s ease;
    }

    .header-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
        gap: 8px;
    }

    .header-col:hover {
        color: #334155;
        background-color: #f1f5f9;
        border-color: #cbd5e1;
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }

    .header-col.active {
        color: #2563eb;
        background-color: #eff6ff;
        border-color: #93c5fd;
    }

    .sort-icon {
        font-size: 14px;
        opacity: 0;
        transition: opacity 0.2s ease;
    }

    .sort-icon.visible {
        opacity: 1;
    }

    .header-col:hover .sort-icon {
        opacity: 0.5;
    }

    .header-col.active .sort-icon {
        opacity: 1;
    }

    .order-container {
        position: relative;
        width: 100%;
        margin-bottom: 10px;
        background: white;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease-in-out;
        border: 1px solid #edf2f7;
        overflow: hidden;
    }

    .order-container:hover {
        border-color: #cbd5e1;
        transform: translateY(-1px);
    }

    .order-container.expanded {
        background: #fff;
        box-shadow: 0 4px 6px rgba(0,0,0,0.05);
    }

    .order-summary {
        display: flex;
        padding: 15px 20px;
        align-items: center;
        position: relative;
        padding-right: 50px; /* Make room for expand icon */
    }

    .expand-icon {
        position: absolute;
        right: 20px;
        top: 50%;
        transform: translateY(-50%);
        color: #94a3b8;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        transition: background-color 0.2s ease;
    }

    .expand-icon:hover {
        background-color: rgba(148, 163, 184, 0.1);
    }

    .chevron-icon {
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    .expand-icon.expanded .chevron-icon {
        transform: rotate(-180deg);
    }

    .order-container:hover .expand-icon {
        color: #64748b;
    }

    .col {
        padding-right: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 14px;
        color: #334155;
    }

    .priority-col {
        display: flex;
        align-items: center;
        width: 100px;
    }

    .priority-badge {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        border-radius: 12px;
        background-color: #e2e8f0;
        font-size: 12px;
        font-weight: 500;
        color: #64748b;
        white-space: nowrap;
        transition: all 0.2s ease;
    }

    .priority-badge.high {
        background-color: #fee2e2;
        color: #ef4444;
    }

    .urgent-icon {
        width: 16px;
        height: 16px;
    }

    .priority-badge.high .urgent-icon {
        color: #ef4444;
    }

    .priority-badge .material-icons {
        font-size: 16px;
    }

    .details-container {
        width: 100%;
        padding: 20px;
        border-top: 1px solid #edf2f7;
        background: #f8f9fa;
        display: flex;
        justify-content: center;
    }

    .item-status-container {
        display: flex;
        flex-direction: column;
        gap: 15px;
        width: 100%;
        max-width: 700px;
        animation: fadeIn 0.3s ease-out;
    }

    .item-status-instance {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px;
        background-color: white;
        border-radius: 10px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        transition: transform 0.2s ease;
    }

    .item-status-instance:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 6px rgba(0,0,0,0.07);
    }

    .actual-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        flex: 1;
        margin-right: 20px;
    }

    .item-name {
        font-weight: 600;
        font-size: 14px;
        color: #1e293b;
    }

    .item-amount {
        color: #64748b;
        font-size: 13px;
    }

    .product-type {
        color: #94a3b8;
        font-size: 13px;
        font-style: italic;
    }

    .actual-status {
        display: flex;
        flex-direction: column;
        gap: 8px;
        align-items: flex-end;
        min-width: 180px;
    }

    .dropdown-status {
        width: 100%;
        padding: 8px 12px;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        background-color: white;
        font-size: 13px;
        color: #334155;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .dropdown-status:hover {
        border-color: #94a3b8;
    }

    .dropdown-status:focus {
        outline: none;
        border-color: #3b82f6;
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }

    .status-timestamp {
        font-size: 12px;
        color: #94a3b8;
    }

    .loading {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        padding: 24px;
        color: #64748b;
        font-size: 14px;
        background: white;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }

    .loading-spinner {
        width: 20px;
        height: 20px;
        border: 2px solid #e2e8f0;
        border-top-color: #3b82f6;
        border-radius: 50%;
        animation: spinner 0.6s linear infinite;
    }

    @keyframes spinner {
        to {
            transform: rotate(360deg);
        }
    }

    @keyframes fadeIn {
        from { opacity: 0; }
        to { opacity: 1; }
    }

    :global(body) {
        margin: 0;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        background-color: #f5f7fa;
    }
</style>