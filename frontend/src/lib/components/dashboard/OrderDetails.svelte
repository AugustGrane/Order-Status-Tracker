<script lang="ts">
    import OrderItem from "$lib/components/dashboard/OrderItem.svelte";

    export let order: any;
</script>

<div class="order-items">
    {#each order.items as item}
        <OrderItem item={item}/>
    {/each}
</div>

<style>
    .order-items {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }
</style>
