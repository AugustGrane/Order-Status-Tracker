<script lang="ts">
    import type { OrderDetailsWithStatus } from '$lib/types';
    import StepComponent from "./StepComponent.svelte";

    export let orderItem: OrderDetailsWithStatus;
    export let name: string;
    export let productType: string;
    export let quantity: number;

    // Get the last update time (index 5 corresponds to the last status)
    let date = new Date(orderItem.updated[5]).toLocaleDateString();
    console.log("Date:", date);
</script>

<div class="item">
    <div class="item-title">{name} | {productType} | <i>Antal: {quantity}</i></div>
    <div class="timeline-wrapper">
        {#if orderItem}
            {#if orderItem.differentSteps.length === 0}
                <p style="color: red">Error: No steps in item</p>
            {:else}
                {#each orderItem.differentSteps as step, index}
                    {#if index === 0}
                        {#if orderItem.currentStepIndex === index}
                            <StepComponent status={step.name} estimate={date} icon={step.image} current={true} firstItem={true} />
                        {:else}
                            <StepComponent status={step.name} estimate={date} icon={step.image} done={true} firstItem={true} />
                        {/if}
                    {:else if index <= orderItem.currentStepIndex}
                        {#if orderItem.currentStepIndex === index}
                            <StepComponent status={step.name} estimate={date} icon={step.image} current={true} done={true} />
                        {:else}
                            <StepComponent status={step.name} estimate={date} icon={step.image} done={true}/>
                        {/if}
                    {:else}
                        {#if orderItem.currentStepIndex === index}
                            <StepComponent status={step.name} estimate={date} icon={step.image} current={true} />
                        {:else}
                            <StepComponent status={step.name} estimate={date} icon={step.image} />
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
        height: 150px;
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
</style>
