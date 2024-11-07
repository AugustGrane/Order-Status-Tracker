<script lang="ts">
    import type { OrderDetailsWithStatus } from '$lib/types';
    import StepComponent from "./StepComponent.svelte";
    import {onMount} from "svelte";

    export let orderItem: OrderDetailsWithStatus;
    export let name: string;
    export let quantity: number;

    let isLastStep = false;

    onMount(() => {
        // Check if this item is at the last step
        isLastStep = orderItem.currentStepIndex === orderItem.differentSteps.length - 1;
    });
</script>

<div class="item">
    <div class="item-title">Artikel #{orderItem.item.id} | {name} | Antal: {quantity}</div>
    <div class="timeline-wrapper">
        {#if orderItem}
            {#if orderItem.differentSteps.length === 0}
                <p style="color: red">Error: No steps in item</p>
            {:else}
                {#each orderItem.differentSteps as step, index}
                    {#if index === 0}
                        {#if orderItem.currentStepIndex === index}
                            <StepComponent status={step.name} icon={step.image} current={true} firstItem={true} />
                        {:else}
                            <StepComponent status={step.name} icon={step.image} done={true} firstItem={true} />
                        {/if}
                    {:else if index <= orderItem.currentStepIndex}
                        {#if orderItem.currentStepIndex === index}
                            <StepComponent status={step.name} icon={step.image} current={true} done={true} />
                        {:else}
                            <StepComponent status={step.name} icon={step.image} done={true}/>
                        {/if}
                    {:else}
                        {#if orderItem.currentStepIndex === index}
                            <StepComponent status={step.name} icon={step.image} current={true} />
                        {:else}
                            <StepComponent status={step.name} icon={step.image} />
                        {/if}
                    {/if}
                {/each}
            {/if}
        {/if}
    </div>
</div>

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

    .item {
        display: flex;
        flex-direction: column;
        height: 170px;
        align-items: flex-start;
        justify-content: center;
        gap: 20px;
        padding: 5px 10px;
        position: relative;
        align-self: stretch;
        width: 100%;
        border-radius: 5px;
        overflow: hidden;
        border: 1px solid;
        border-color: #00000026;
    }

    .item-title {
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-size: 1.25rem;
        font-weight: 400;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .timeline-wrapper {
        display: flex;
        align-items: flex-start;
        justify-content: center;
        position: relative;
        align-self: stretch;
        width: 100%;
        flex: 0 0 auto;
    }
    @media (max-width: 1000px) {
        .item-title{
            font-size: 0.9rem;

        }
        .item {
            height: 120px;
            align-items: center;
        }

    }
</style>
