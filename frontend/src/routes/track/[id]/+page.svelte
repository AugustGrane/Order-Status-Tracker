<script lang="ts">
    import { page } from '$app/stores';
    import { browser } from '$app/environment';
    import type { OrderDetailsWithStatus } from '$lib/types';
    import ItemComponent from '$lib/components/timeline/ItemComponent.svelte';
    import {onMount} from "svelte";
    import {goto} from "$app/navigation";
    import confetti from 'canvas-confetti';
    import TrackForm from "$lib/components/TrackForm.svelte";

    export let data: { 
        order: OrderDetailsWithStatus[] | null;
        orderNotFound: boolean;
        orderId: string;
    };

    let previousAllItemsComplete = false;

    // Check if all items in the order are at their last step
    $: allItemsComplete = data.order?.every(item => 
        item.currentStepIndex === item.differentSteps.length - 1
    ) ?? false;

    // Trigger confetti when allItemsComplete becomes true
    $: if (browser && allItemsComplete && !previousAllItemsComplete) {
        previousAllItemsComplete = true;
        confetti({
            particleCount: 200,
            spread: 200,
            origin: { y: 0.6 },
            colors: ['#24A147', '#1166ee', '#FFC107']
        });
    }

    onMount(() => {
        if (browser && allItemsComplete) {
            previousAllItemsComplete = true;
            confetti({
                particleCount: 200,
                spread: 200,
                origin: { y: 0.6 },
                colors: ['#24A147', '#1166ee', '#FFC107']
            });
        }
    });
</script>

{#if data.orderNotFound}
    <TrackForm initialValue={data.orderId} initialError="Ordrenummer findes ikke" />
{:else}
    <div class="main2">
        <div class="background2">
            <div class="logo2"></div>
            <button class="backbutton" on:click={() => goto("/track")}>{"←"} Track en anden ordre</button>
            <div class="order-box-main">
                <div class="title-wrapper">
                    <div class="order-number-text">Ordrenummer: #{data.orderId}</div>
                    {#if allItemsComplete}
                        <div class="order-sent">Ordren er sendt</div>
                    {/if}
                    <div class="circle-explanations">
                        <div class="circle-explanation">
                            <div class="circle"></div>
                            <div class="circle-text">Færdig</div>
                        </div>
                        <div class="circle-explanation">
                            <div class="current-circle"></div>
                            <div class="circle-text">Igangsat</div>
                        </div>
                        <div class="circle-explanation">
                            <div class="circle-2"></div>
                            <div class="circle-text">Afventer</div>
                        </div>
                    </div>
                </div>
                <div class="order-box-items">
                    {#if data.order}
                        {#if data.order.length === 0}
                            <p style="color: red">No items found for this order.</p>
                        {:else}
                            {#each data.order as item (item.id)}
                                <ItemComponent 
                                    orderItem={item} 
                                    name={item.item.name} 
                                    quantity={item.itemAmount}
                                />
                            {/each}
                        {/if}
                    {:else}
                        <p>Loading order details...</p>
                    {/if}
                </div>
            </div>
        </div>
    </div>
{/if}

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    :root {
        font-size: 16px;
        --font-primary: 'Roboto', Arial, sans-serif;
    }

    *,
    *::before,
    *::after {
        box-sizing: border-box;
    }

    .main2 {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
        align-items: center;
        gap: 10px;
        position: relative;
        background-color: #ffffff;
    }

    .background2 {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 0px 10%;
        position: relative;
        flex: 1;
        align-self: stretch;
        width: 100%;
        background-color: #dbdbdb4c;
    }

    .logo2 {
        position: relative;
        width: 12rem;
        height: 7rem;
        margin: 18px;
        object-fit: cover;
        background: url('/gtryk_logo.png') no-repeat center;
        background-size: contain;
    }

    .backbutton {
        position: absolute;
        top: 20px;
        left: 20px;
        padding: 0.5rem 1rem;
        border-radius: 0.5vw;
        background: #454545;
        color: #FFF;
        opacity: 0.3;
        font-family: var(--font-primary);
        font-size: 1.0rem;
        font-weight: 250;
        border: none;
        cursor: pointer;
        text-align: center;
    }

    .order-box-main {
        align-items: flex-start;
        padding: 15px 20px;
        background-color: #ffffff;
        border-radius: 15px;
        overflow: hidden;
        border: none;
        display: flex;
        flex-direction: column;
        position: relative;
        align-self: stretch;
        width: 100%;
        flex: 0 0 auto;
    }

    .title-wrapper {
        gap: 10px;
        margin-bottom: 0.5rem;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: flex-start;
        position: relative;
        align-self: stretch;
        width: 100%;
        flex: 0 0 auto;
    }

    .order-number-text {
        position: relative;
        width: fit-content;
        margin-top: -1.00px;
        font-family: var(--font-primary);
        font-size: 2.0rem;
        font-weight: 500;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .order-sent {
        position: absolute;
        top: 10px;
        right: 220px;
        font-family: var(--font-primary);
        font-size: 1.2rem;
        color: #24A147;
        font-weight: 500;
    }

    .order-box-items {
        align-items: center;
        gap: 10px;
        padding: 10px 0px 52px;
        display: flex;
        flex-direction: column;
        position: relative;
        align-self: stretch;
        width: 100%;
        flex: 0 0 auto;
    }

    .circle-explanations {
        display: flex;
        width: 200px;
        justify-content: center;
        align-items: center;
        gap: 5px;
    }

    .circle-explanation {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 5px;
        flex: 1 0 0;
    }

    .circle {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #24a147;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .current-circle {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #1166ee;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .circle-2 {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #aaaaaa;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .circle-text {
        color: #000;
        font-family: var(--font-primary);
        font-size: 0.9rem;
        font-style: normal;
        font-weight: 400;
        line-height: normal;
    }
</style>
