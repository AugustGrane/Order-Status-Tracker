<script lang="ts">
    export let order: any;

    async function saveItem(item: any) {

        // Data to be sent to the backend
        let orderDetailsId =  item.id; // ID of the item
        let newStepIndex = item.currentStepIndex; // Updated step inde

        //console.log(data);

        try {
            const response = await fetch('/api/update-step', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ orderDetailsId, newStepIndex }),

            });

            if (!response.ok) {
                throw new Error(`Fejl ved opdatering af trin for item ID ${item.id}: ${response.statusText}`);
            }

            const result = await response.json();
            console.log('Opdatering succesfuld:', result);
            alert('Trinnet blev opdateret!');

        } catch (error) {
            console.error('Fejl ved opdatering:', error);
            alert('Kunne ikke opdatere trinnet. Prøv igen.');
        }
    }


    function updateStep(item: any, newStepIndex: number) {
        // Logik til opdatering af trin, hvis nødvendigt
        console.log(`Opdaterer trin for ${item.item.name} til ${item.differentSteps[newStepIndex].name}`);
        item.currentStepIndex = newStepIndex; // Opdatering af det nuværende trin
    }


</script>

<div class="order-items">
    {#each order.items as item}
        <div class="item">
            <div class="item-header">

                <h4>{item.item.name} - {item.itemAmount}</h4>
                <span class="product-type">{item.productTypeName}</span>
            </div>

            <div class="item-content">
                <div class="progress-steps">
                    {#each item.differentSteps as step, index}
                        <div class="step" class:active={index <= item.currentStepIndex}>
                            <div class="step-content">
                                <div class="step-marker">
                                    <div class="icon" style="background: url('/{step.image.replace('frontend/static/', '')}') no-repeat center;"></div>
                                </div>
                                <span class="step-name">{step.name}</span>
                            </div>
                            {#if index<item.differentSteps.length-1}
                                <div class="step-line"></div>
                            {/if}
                        </div>
                    {/each}
                </div>
          
                <div class="select-step">
                    <select bind:value={item.currentStepIndex} on:change={(e) => updateStep(item, e.target.value)}>
                        {#each item.differentSteps as step, index}
                            <option value={index}>{step.name}</option>
                        {/each}
                    </select>
                </div>
          
                <div class="save-button">
                    <button on:click={() => saveItem(item)}>Gem</button>
                </div>
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
        justify-content: flex-start;
        align-items: center;
        margin-bottom: 1rem;
    }

    .item-header h4 {
        margin-right: 1rem;
        font-size: 1rem;
        color: #1e293b;
    }

    .item-content {
        display: flex;
        justify-content: flex-start;
        align-items: center;
        gap: 1rem;
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
        width: 70%;
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

    select {
        width: 15rem;
        padding: 0.75rem 2rem 0.75rem 1rem;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        background-color: white;
        font-size: 0.9rem;
        cursor: pointer;
    }

    .select-step {
        font-size: 0.875rem;
        width: 15rem;
    }

    .save-button {
    }

    .save-button button {
        display: flex;
        justify-content: center;
        align-items: center;
        background: #3b82f6;
        color: white;
        border-radius: 8px;
        cursor: pointer;
        font-size: 0.875rem;
        padding: 0.75rem 2rem;
        border: 1px solid #e2e8f0;
        text-align: center;
    }

    .save-button button:hover {
        background: #2563eb;
    }


</style>
