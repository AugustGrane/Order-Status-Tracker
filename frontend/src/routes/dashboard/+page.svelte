<script lang="ts">
    import type { PageData } from './$types';
    import { onMount } from 'svelte';
    
    export let data: PageData;
    let orders = data.orders;
    let orderDetails = data.initialDetails;
    
    const loadOrderDetails = async (orderId: number) => {
        if (!orderDetails[orderId]) {
            try {
                const response = await fetch(`/api/orders/${orderId}/details`);
                const details = await response.json();
                orderDetails[orderId] = details;
                orderDetails = orderDetails; // Trigger reactivity
            } catch (error) {
                console.error('Error loading order details:', error);
            }
        }
    };

    const formatDate = (dateStr: string) => {
        const date = new Date(dateStr);
        return date.toLocaleDateString('da-DK', { 
            year: 'numeric', 
            month: '2-digit', 
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    };
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
                    <div class="search"><input class="search-field" placeholder="Søg efter navn, ordrenummer eller artikler"></div>
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
                    <div class="items-and-amount">Antal artikler</div>
                    <div class="priority">Prioritet</div>
                </div>
                <div class="order">
                    {#each orders as order}
                        <div class="order-container" 
                             class:priority={order.priority}
                             on:mouseenter={() => loadOrderDetails(order.orderId)}>
                            <div class="actual-order-number">{order.orderId}</div>
                            <div class="actual-date">{formatDate(order.orderCreated)}</div>
                            <div class="actual-customer">{order.customerName}</div>
                            <div class="actual-items">{order.totalItems} artikler</div>
                            <div class="actual-priority">{order.priority ? 'Ja' : 'Nej'}</div>
                            
                            {#if orderDetails[order.orderId]}
                                <div class="details-container">
                                    {#each orderDetails[order.orderId].items || [] as item}
                                        <div class="item-status-instance">
                                            <div class="actual-item">{item.item.name} - {item.itemAmount}</div>
                                            <div class="actual-status">
                                                <select class="dropdown-status">
                                                    {#each item.differentSteps as step}
                                                        <option>{step}</option>
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
    </div>
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    html {
        font-family: Roboto, Arial, sans-serif;
    }

    .background {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 25px;
        position: relative;
        background-color: #ffffff;
        padding-bottom: 25px;

    }

    .background .navbar {
        display: flex;
        height: 81px;
        align-items: center;
        justify-content: center;
        gap: 10px;
        width: 100%;
        background-color: #ffffff;
    }

    .background .logo {
        position: relative;
        width: 6rem;
        height: 3.5rem;
        background-size: contain;
        background-image: url('/gtryk_logo.png');
        background-size: 100% 100%;
    }

    .background .title {
        position: relative;
        width: 234px;
        height: 53px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 44px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .container {
        display: flex;
        flex-direction: column;
        align-items: center; /* Centering content inside */
        padding: 5px;
        width: 90%; /* Adjust as needed */
        background-color: #f9f9f9; /* Background color for visibility */
        border-radius: 15px;
        box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
    }

    .background .search-filter {
        display: flex;
        height: 100px;
        width: 1090px;
        align-items: flex-end;
        justify-content: space-between;
        position: relative;
        padding-top: 25px;
    }

    .background .left {
        display: flex;
        flex-direction: column;
        width: 535px;
        height: 100px;
        align-items: flex-start;
        justify-content: space-between;
        position: relative;
        margin-top: -5.00px;
        border: 0px none;
    }

    .background .active-orders-text {
        width: 262px;
        height: 24px;
        font-size: 24px;
        position: relative;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .background .active-orders {
        font-size: 16px;
        position: relative;
        width: 397px;
        height: 16px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .background .search {
        display: flex;
        width: 200px;
        flex-direction: column;
        align-items: flex-start;
        justify-content: flex-end;
        gap: 10px;
        padding: 5px 0px;
        position: relative;
        flex: 0 0 auto;
        border-radius: 7px;
        border: 1px;
    }

    .background .search-field {
        position: relative;
        width: 300px;
        height: 25px;
        margin-top: -2.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 300;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
        border-radius: 7px;
    }

    .background .right {
        display: flex;
        flex-direction: column;
        width: 524px;
        align-items: flex-end;
        justify-content: flex-end;
        gap: 10px;
        padding: 5px 0px;
        position: relative;
        flex: 0 0 auto;
    }

    .background .dropdown-filter {
        width: 200px;
        height: 32px;
        position: relative;
        margin-top: -2.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 300;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
        border-radius: 7px;
    }

    .background .order-overview {
        display: flex;
        flex-wrap: wrap;
        width: 1090px;
        gap: 10px;
        align-items: flex-start;
        justify-content: center;
        position: relative;
    }

    .background .name-bar {
        display: flex;
        width: 1090px;
        height: 35px;
        align-items: center;
        gap: 10px;
        padding: 0px 25px;
        position: relative;
        background-color: #9d9d9d;
        border-radius: 15px;
    }

    .background .order-number {
        position: relative;
        width: 160px;
        height: 19px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .background .date {
        position: relative;
        width: 125px;
        height: 19px;
        font-family: "Inter-Regular", Helvetica;
        font-width: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .background .customer {
        position: relative;
        width: 200px;
        height: 19px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .status {
        position: relative;
        height: 19px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .items-and-amount {
        position: relative;
        width: 275px;
        height: 19px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .order {
        display: flex;
        flex-direction: column;
        width: 1090px;
        align-items: flex-start;
        gap: 10px;
        position: relative;
        padding-bottom: 25px; /* Remove fixed height */
    }

    .background .order-container {
        width: 1040px;
        padding: 20px 25px;
        background-color: #c3c3c3;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        border-radius: 15px;
        border: none;
    }

    .background .actual-order-number {
        position: relative;
        width: 160px;
        height: 19px;
        margin-top: -1.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .actual-date {
        position: relative;
        width: 125px;
        height: 19px;
        margin-top: -1.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .actual-customer {
        position: relative;
        width: 200px;
        height: 19px;
        margin-top: -1.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
    }

    .background .item-status-container {
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .background .item-status-instance {
        display: flex;
        width: 520px;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        background-color: #9d9d9d;
        padding: 5px 10px;
        border-radius: 15px;
    }

    .background .actual-item {
        position: relative;
        font-family: var(--font-primary);
        font-weight: 400;
        color: #000000;
        font-size: 16px;

    }

    .background .actual-status {
        display: flex;
        justify-content: flex-end;
        position: relative;
    }

    .background .dropdown-status {
        width: 235px;
        min-width: 150px;
        font-size: 16px;
        white-space: nowrap;
        position: relative;
        font-family: var(--font-primary);
        font-weight: 400;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
        padding: 5px;
        border-radius: 15px;
    }

    .order-container {
        display: flex;
        padding: 15px;
        border-bottom: 1px solid #eee;
        align-items: center;
        transition: background-color 0.2s;
    }

    .order-container:hover {
        background-color: #f5f5f5;
    }

    .order-container.priority {
        background-color: #fff3e0;
    }

    .order-container > div {
        flex: 1;
        padding: 0 10px;
    }

    .actual-priority {
        color: #666;
    }

    .details-container {
        display: none;
        position: absolute;
        left: 0;
        right: 0;
        background: white;
        border: 1px solid #ddd;
        padding: 15px;
        margin-top: 10px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .order-container:hover .details-container {
        display: block;
    }
</style>