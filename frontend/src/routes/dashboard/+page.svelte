<script lang="ts">
    import type { PageData } from './$types';
    export let data: PageData;
    const orders = data.orders; // If you want to create a shorter reference
    console.log(orders);
</script>

<main>
    <h1>Dashboard</h1>
    <p>Here you can see all orders</p>
    {#if orders} <!-- Now we can check just orders -->
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
    {/if}
</main>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    html {
        font-family: Roboto, Arial, sans-serif;
    }

    main {
        margin: 0 20px;
    }
</style>