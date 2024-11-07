<script>
    import {onMount} from "svelte";

    let data;

    onMount(async () => {
        try {
            const response = await fetch("http://localhost:8080/api/get-all-orders", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            data = await response.json();
            console.log(data);
        } catch (error) {
            console.error(error);
        }
    });
</script>

<h1>Dashboard</h1>

<p>Here you can see all orders</p>

{#if data}
    {#each data as order}
        <p>Item: {order.orderId}</p>
        <p>Amount: {order.itemAmount}</p>
        <p>Date created: {new Date(order.orderCreated).toLocaleDateString()}</p>
        <p>Customer name: {order.customerName}</p>
        <p>Notes: {order.Notes}</p>
        <p>Status: {order.statusDefination}</p>
    {/each}
{:else}
    <p>Loading...</p>
{/if}