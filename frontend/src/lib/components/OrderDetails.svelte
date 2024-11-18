<script lang="ts">
    export let order: any;
</script>

<div class="order-items">
    {#each order.items as item}
        <div class="item">
            <div class="item-header">
                <h4>{item.item.name} - {item.itemAmount}</h4>
                <span class="product-type">{item.product_type}</span>
            </div>
            
            <div class="progress-steps">
                {#each item.differentSteps as step, index}
                    <div class="step" class:active={index <= item.currentStepIndex}>
                        <div class="step-content">
                            <div class="step-marker">
                                <div class="icon" style="background: url('/{step.image.replace('frontend/static/', '')}') no-repeat center;"></div>
                            </div>
                            <span class="step-name">{step.name}</span>
                        </div>
                        {#if index < item.differentSteps.length - 1}
                            <div class="step-line"></div>
                        {/if}
                    </div>
                {/each}
            </div>
        </div>
    {/each}
</div>

<style>
    .order-items {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .item {
        background: white;
        border-radius: 8px;
        padding: 1rem;
    }

    .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .item-header h4 {
        margin: 0;
        font-size: 1rem;
        color: #1e293b;
    }

    .product-type {
        font-size: 0.875rem;
        color: #64748b;
        padding: 0.25rem 0.75rem;
        background: #f1f5f9;
        border-radius: 9999px;
    }

    .progress-steps {
        display: flex;
        align-items: center;
        margin-top: 1rem;
    }

    .step {
        flex: 1;
        display: flex;
        align-items: center;
    }

    .step-content {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.5rem;
        z-index: 1;
    }

    .step-marker {
        width: 2rem;
        height: 2rem;
        border-radius: 50%;
        background: #f8fafc;
        border: 2px solid #e2e8f0;
        display: flex;
        align-items: center;
        justify-content: center;
        overflow: hidden;
    }

    .step.active .step-marker {
        border-color: #3b82f6;
    }

    .icon {
        width: 100%;
        height: 100%;
        background-size: contain !important;
        filter: grayscale(100%) opacity(0.5);
    }

    .step.active .icon {
        filter: none;
    }

    .step-name {
        font-size: 0.75rem;
        color: #64748b;
        text-align: center;
        max-width: 100px;
    }

    .step.active .step-name {
        color: #1e293b;
        font-weight: 500;
    }

    .step-line {
        flex: 1;
        height: 2px;
        background: #e2e8f0;
        margin: 0 0.5rem;
    }

    .step.active .step-line {
        background: #3b82f6;
    }
</style>
