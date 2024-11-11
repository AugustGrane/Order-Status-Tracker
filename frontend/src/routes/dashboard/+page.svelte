<script lang="ts">
    import type { PageData } from './$types';
    export let data: PageData;
    const orders = data.orders; // If you want to create a shorter reference
    console.log(orders);
    
</script>

<main>
    <div class="background">
        <div class="navbar">
            <div class="logo"></div>
            <div class="title">Dashboard</div>
        </div>
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
                <div class="items-and-amount">Artikler og mængde</div>
                <div class="status">Artikelstatus</div> <!-- Changed class name here -->
            </div>
            <div class="order">
                {#each orders as order}
                    <div class="order-container">
                        <div class="actual-order-number">{order.orderId}</div>
                        <div class="actual-date">{new Date(order.orderCreated).toLocaleDateString()}</div>
                        <div class="actual-customer">{order.customerName}</div>
                        <div class="item-container">
                            {#each order.items as item}
                                <div class="actual-item">{item.item.name} - {item.itemAmount}</div>
                            {/each}
                        </div>
                        <div class="status-container">
                            {#each order.items as item}
                                <div class="actual-status">
                                    <select class="dropdown-status">
                                        {#each item.differentSteps as step}
                                            {#if step.name == item.differentSteps[item.currentStepIndex].name}
                                                <option selected>{item.differentSteps[item.currentStepIndex].name}&nbsp;</option>
                                            {:else}
                                                <option>{step.name}&nbsp;</option>
                                            {/if}
                                        {/each}
                                    </select>
                                </div>
                            {/each}
                        </div>
                    </div>
                {/each}
            </div>
        </div>
    </div>
</main>

<style>

    /*{#if orders} <!-- Now we can check just orders -->
        {#each orders as order}
            <p>Order ID: {order.orderId}</p>
            <p>Date created: {new Date(order.orderCreated).toLocaleDateString()}</p>
            <p>Customer name: {order.customerName}</p>
            <h3>Products:
                {#each order.items as item}
                    Name: {item.item.name},
                    Amount: {item.itemAmount},
                    Status: {item.differentSteps[item.currentStepIndex].name}&nbsp;
                {/each}
            </h3>
            <p>Notes: {order.Notes}</p>
            <br>
        {/each}
    {:else}
        <p>Loading...</p>
    {/if}*/

    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

      html {
        font-family: Roboto, Arial, sans-serif;
    }

    .background {
        display: flex;
        flex-direction: column;
        height: 1024px;
        align-items: center;
        gap: 5px;
        position: relative;
        background-color: #ffffff;
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

    .background .search-filter {
        display: flex;
        height: 100px;
        width: 1090px;
        align-items: flex-end;
        justify-content: space-between;
        position: relative;
        background-color: #ffffff;
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
    }

    .background .search-field {
        position: relative;
        width: 300px;
        height: 25px;
        margin-top: -2.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 300;
        color: #8b8b8b;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
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
    }

    .background .order-overview {
        display: flex;
        flex-wrap: wrap;
        width: 1090px;
        height: 550px;
        gap: 10px;
        align-items: flex-start;
        justify-content: center;
        position: relative;
        background-color: #ffffff;
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
        background-color: #ffffff;
        padding-bottom: 25px; /* Remove fixed height */
    }

    .background .order-container {
        width: 1040px;
        padding: 20px 25px;
        background-color: #c3c3c3;
        display: flex;
        flex-direction: row;
        align-items: flex-start;
        gap: 10px;
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

    .background .item-container {
        display: flex;
        flex-direction: column;
        width: 275px;
    }

    .background .actual-item {
        position: relative;
        width: 250px;
        height: 35px;
        margin-bottom: -9.00px;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        font-size: 16px;
        letter-spacing: 0;
        line-height: normal;
        padding-bottom: 3px;
    }

    .background .status-container {
        display: flex;
        flex-direction: column;
        width: 200px;
    }

    .background .actual-status {
        display: flex;
        width: 200px;
        min-width: 30px;
        max-width: 30px;
        align-items: center;
        gap: 10px;
        position: relative;
        padding-bottom: 7px;
    }

    .background .dropdown-status {
        width: fit-content;
        min-width: 150px;
        font-size: 16px;
        white-space: nowrap;
        position: relative;
        font-family: "Inter-Regular", Helvetica;
        font-weight: 400;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
    }

</style>