<script lang="ts">
    import type { PageData } from './$types';
    export let data: PageData;
    const orders = data.orders;
    
    // Track expanded state for each order
    let expandedOrders: { [key: string]: boolean } = {};
    
    function toggleOrder(orderId: string) {
        expandedOrders[orderId] = !expandedOrders[orderId];
        expandedOrders = expandedOrders; // Trigger reactivity
    }

    function getProgressPercentage(currentStep: number, totalSteps: number): number {
        return (currentStep / (totalSteps - 1)) * 100;
    }
</script>

<main>
    <div class="background">
        <div class="navbar">
            <div class="logo"></div>
            <div class="title">Dashboard</div>
        </div>
        <div class="container">
            <div class="search-filter">
                <div class="left">
                    <div class="active-orders-text">Aktive ordre</div>
                    <div class="active-orders">Se og opdater kundeordre</div>
                    <div class="search">
                        <input class="search-field" placeholder="Søg efter navn, ordrenummer eller artikler">
                        <span class="search-icon">🔍</span>
                    </div>
                </div>
                <div class="right">
                    <select class="dropdown-filter">
                        <option>Nyeste</option>
                        <option>Ældste</option>
                        <option>Længst i process</option>
                    </select>
                </div>
            </div>
            <div class="order-overview">
                <div class="name-bar">
                    <div class="order-number">Ordrenummer</div>
                    <div class="date">Dato</div>
                    <div class="customer">Kundens navn</div>
                    <div class="items-and-amount">Artikler og mængde</div>
                    <div class="status">Artikelstatus</div>
                </div>
                <div class="orders-list">
                    {#each orders as order}
                        <div class="order-container" class:expanded={expandedOrders[order.orderId]}>
                            <div class="order-header" on:click={() => toggleOrder(order.orderId)}>
                                <div class="actual-order-number">{order.orderId}</div>
                                <div class="actual-date">{new Date(order.orderCreated).toLocaleDateString()}</div>
                                <div class="actual-customer">{order.customerName}</div>
                                <div class="items-summary">
                                    {order.items.length} {order.items.length === 1 ? 'artikel' : 'artikler'}
                                </div>
                                <div class="expand-icon">
                                    {expandedOrders[order.orderId] ? '▼' : '▶'}
                                </div>
                            </div>
                            
                            {#if expandedOrders[order.orderId]}
                                <div class="order-details">
                                    {#each order.items as item}
                                        <div class="item-detail">
                                            <div class="item-info">
                                                <h4>{item.item.name}</h4>
                                                <p>Antal: {item.itemAmount}</p>
                                            </div>
                                            <div class="progress-container">
                                                <div class="progress-steps">
                                                    {#each item.differentSteps as step, index}
                                                        <div class="step" class:active={index <= item.currentStepIndex}>
                                                            <div class="step-dot"></div>
                                                            <span class="step-name">{step.name}</span>
                                                        </div>
                                                    {/each}
                                                </div>
                                                <div class="progress-bar">
                                                    <div class="progress" style="width: {getProgressPercentage(item.currentStepIndex, item.differentSteps.length)}%"></div>
                                                </div>
                                                <div class="status-selector">
                                                    <select class="dropdown-status">
                                                        {#each item.differentSteps as step}
                                                            <option selected={step.name === item.differentSteps[item.currentStepIndex].name}>
                                                                {step.name}
                                                            </option>
                                                        {/each}
                                                    </select>
                                                </div>
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
    </div>
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap');

    :global(body) {
        margin: 0;
        font-family: 'Roboto', sans-serif;
        background-color: #f5f5f5;
    }

    .background {
        min-height: 100vh;
        padding: 20px;
        background-color: #f5f5f5;
    }

    .navbar {
        background-color: white;
        padding: 1rem 2rem;
        width: 100%;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        display: flex;
        align-items: center;
        margin-bottom: 2rem;
        border-radius: 8px;
    }

    .title {
        font-size: 1.5rem;
        font-weight: 500;
        color: #333;
    }

    .container {
        max-width: 1200px;
        margin: 0 auto;
    }

    .search-filter {
        background-color: white;
        padding: 1.5rem;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        margin-bottom: 1.5rem;
    }

    .search {
        position: relative;
    }

    .search-field {
        width: 100%;
        padding: 0.8rem 1rem;
        border: 1px solid #e0e0e0;
        border-radius: 6px;
        font-size: 0.9rem;
        transition: border-color 0.2s;
    }

    .search-field:focus {
        outline: none;
        border-color: #2196f3;
    }

    .search-icon {
        position: absolute;
        right: 1rem;
        top: 50%;
        transform: translateY(-50%);
        color: #666;
    }

    .dropdown-filter {
        padding: 0.8rem 1rem;
        border: 1px solid #e0e0e0;
        border-radius: 6px;
        background-color: white;
        cursor: pointer;
    }

    .order-container {
        background-color: white;
        border-radius: 8px;
        margin-bottom: 1rem;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        transition: all 0.3s ease;
    }

    .order-container:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }

    .order-header {
        display: flex;
        padding: 1rem;
        cursor: pointer;
        align-items: center;
    }

    .order-details {
        padding: 1rem;
        border-top: 1px solid #eee;
        animation: slideDown 0.3s ease-out;
    }

    @keyframes slideDown {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .item-detail {
        padding: 1rem;
        border-radius: 6px;
        background-color: #f8f9fa;
        margin-bottom: 1rem;
    }

    .progress-container {
        margin-top: 1rem;
    }

    .progress-steps {
        display: flex;
        justify-content: space-between;
        margin-bottom: 0.5rem;
        position: relative;
    }

    .step {
        display: flex;
        flex-direction: column;
        align-items: center;
        position: relative;
        z-index: 1;
    }

    .step-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background-color: #e0e0e0;
        margin-bottom: 0.5rem;
        transition: background-color 0.3s;
    }

    .step.active .step-dot {
        background-color: #2196f3;
    }

    .step-name {
        font-size: 0.8rem;
        color: #666;
    }

    .progress-bar {
        height: 4px;
        background-color: #e0e0e0;
        border-radius: 2px;
        margin: 1rem 0;
        position: relative;
    }

    .progress {
        height: 100%;
        background-color: #2196f3;
        border-radius: 2px;
        transition: width 0.3s ease;
    }

    .dropdown-status {
        padding: 0.5rem;
        border: 1px solid #e0e0e0;
        border-radius: 4px;
        background-color: white;
        cursor: pointer;
    }

    .expand-icon {
        margin-left: auto;
        color: #666;
        transition: transform 0.3s;
    }

    .expanded .expand-icon {
        transform: rotate(180deg);
    }

    .items-summary {
        color: #666;
        font-size: 0.9rem;
    }

    h4 {
        margin: 0 0 0.5rem 0;
        color: #333;
    }

    p {
        margin: 0;
        color: #666;
    }
</style>