<script lang="ts">
    import {onMount} from "svelte";
    import type { OrderDetailsWithStatus } from "$lib/types";

    let order_id = "8080";
    // export let data: { order: OrderDetailsWithStatus[] };
    let data;
    let updatedData;

    onMount(async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/orders/${order_id}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            if (!response.ok) {
                throw new Error('Order not found');
            }

            data = await response.json();
            data.sort((a, b) => a.id - b.id);
            console.log("Response: ", data);
        } catch (error) {
            console.error(error);
        }
    });

    async function nextStep(item) {
        try {
            const response = await fetch(`http://localhost:8080/api/order-product-types/${item}/next-step`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    step_id: order_id
                })
            });

            if (!response.ok) {
                throw new Error('Could not move to next step');
            }

            updatedData = await response.json();
            console.log("Updated data: ", updatedData);
        } catch (error) {
            console.error(error);
        }
    }

    async function prevStep(item) {
        try {
            const response = await fetch(`http://localhost:8080/api/order-product-types/${item}/prev-step`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    step_id: order_id
                })
            });

            if (!response.ok) {
                throw new Error('Could not move to next step');
            }

            updatedData = await response.json();
            console.log("Updated data: ", updatedData);
        } catch (error) {
            console.error(error);
        }
    }
</script>

{#each data as item (item.id)}
    <div>
        <div>Item: {item.item.id}</div>
        <div>Item name: {item.item.name}</div>
        <div>Item amount: {item.itemAmount}</div>
        <div>Current step: {item.currentStepIndex}</div>
        <div>Steps: [{#each item.differentSteps as step}{step.name},&nbsp{/each}]</div>
    </div>
    <button onclick="{prevStep(item.id)}">Move to previous step</button>
    <button onclick="{nextStep(item.id)}">Move to next step</button>
    <br>
    <br>
{/each}