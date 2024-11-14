<script lang="ts">
    import { onMount } from 'svelte';
    import { slide } from 'svelte/transition';
    import { quintOut } from 'svelte/easing';

    let orders = [];
    let orderDetails = {};
    let expandedOrderId = null;
    let loadingOrderId = null;

    async function loadOrders() {
        try {
            const response = await fetch('/api/orders/summaries');
            if (!response.ok) throw new Error('Failed to fetch orders');
            orders = await response.json();
        } catch (error) {
            console.error('Error loading orders:', error);
        }
    }

    async function loadOrderDetails(orderId) {
        if (!orderDetails[orderId]) {
            loadingOrderId = orderId;
            try {
                const response = await fetch(`/api/orders/${orderId}`);
                if (!response.ok) throw new Error('Failed to fetch order details');
                // Add artificial delay for smooth transition
                await new Promise(resolve => setTimeout(resolve, 300));
                orderDetails[orderId] = await response.json();
                orderDetails = orderDetails; // Trigger reactivity
            } catch (error) {
                console.error('Error loading order details:', error);
            } finally {
                loadingOrderId = null;
            }
        }
    }

    async function toggleOrderDetails(orderId) {
        if (expandedOrderId === orderId) {
            expandedOrderId = null;
        } else {
            expandedOrderId = orderId;
            await loadOrderDetails(orderId);
        }
    }

    function handleKeyDown(event, orderId) {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            toggleOrderDetails(orderId);
        }
    }

    async function handleStatusChange(id, newStatus) {
        try {
            const response = await fetch(`/api/order-product-types/${id}/next-step`, {
                method: 'POST'
            });
            if (!response.ok) throw new Error('Failed to update status');
            await loadOrderDetails(expandedOrderId);
        } catch (error) {
            console.error('Error updating status:', error);
        }
    }

    function formatDate(dateString) {
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

    onMount(() => {
        loadOrders();
    });
</script>

<main>
    <div class="dashboard">
        <div class="dashboard-header">
            <h1>Ordre Oversigt</h1>
            <button 
                class="refresh-button" 
                on:click={loadOrders}
                aria-label="Opdater ordreliste"
            >
                <span class="material-icons">refresh</span>
            </button>
        </div>
        <div class="table-container">
            <div class="header">
                <div class="col" style="width: 150px;">Ordrenummer</div>
                <div class="col" style="width: 150px;">Dato</div>
                <div class="col" style="width: 250px;">Kundens navn</div>
                <div class="col" style="width: 150px;">Antal artikler</div>
                <div class="col" style="width: 100px;">Prioritet</div>
            </div>
            {#each orders as order}
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
                            <span class="priority-badge" class:high={order.priority === 'HIGH'}>
                                {#if order.priority === 'HIGH'}
                                    <span class="material-icons">priority_high</span>
                                {/if}
                                {order.priority === 'HIGH' ? 'Høj' : 'Normal'}
                            </span>
                        </div>
                        <div class="expand-icon" class:expanded={expandedOrderId === order.orderId}>
                            <span class="material-icons">
                                {expandedOrderId === order.orderId ? 'expand_less' : 'expand_more'}
                            </span>
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
        justify-content: space-between;
        align-items: center;
        margin-bottom: 30px;
        padding: 0 20px;
    }

    .dashboard-header h1 {
        font-size: 24px;
        font-weight: 600;
        color: #2c3e50;
        margin: 0;
    }

    .refresh-button {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: white;
        cursor: pointer;
        transition: all 0.2s ease;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }

    .refresh-button:hover {
        background-color: #f8f9fa;
        transform: rotate(180deg);
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

    .header {
        display: flex;
        padding: 15px 20px;
        background-color: #f8f9fa;
        border-radius: 8px;
        margin-bottom: 15px;
        font-weight: 600;
        color: #64748b;
        font-size: 13px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
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
    }

    .expand-icon .material-icons {
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        transform: rotate(0deg);
    }

    .expand-icon.expanded .material-icons {
        transform: rotate(-180deg);
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
    }

    .priority-badge.high {
        background-color: #fee2e2;
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