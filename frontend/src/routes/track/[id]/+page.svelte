<script lang="ts">
    import { page } from '$app/stores'; // For accessing the page store
    import { onMount } from 'svelte'; // Importing onMount
    import { orderData } from '$lib/stores/orderStore'; // Import writable store
    import ItemComponent from './components/ItemComponent.svelte'; // Import the Item component

    // Declare a variable to hold the ID
    let id:any;
    // ANY TYPES NEED DEFINING WHEN WE KNOW WHAT TYPE RESPONSE IS
    let errorMessage:any; // To hold any error messages
    // Reactive statement to access the ID from the URL
    $: id = $page.params.id;

    // Use onMount to fetch order details based on the ID
    onMount(async () => {
        console.log("Fetching order details for ID:", id);// Log the ID

        if (id) { // Only fetch if id is present
            try {
                const response = await fetch(`http://localhost:8080/api/orders/${id}`, {
                    method: 'GET', // Set the method to POST to match the backend's expectation
                    headers: {
                        'Content-Type': 'application/json' // Optional, depending on backend requirements
                    }
                }); // Adjust the URL accordingly
                if (response.ok) {
                    orderData.set(await response.json());
                    console.log(`Order Data: ${JSON.stringify($orderData, null, 2)}`);
                    console.log(`Amount of Items: ${JSON.stringify($orderData.orderDetails.length, null, 2)}`);
                } else {
                    errorMessage = 'Failed to retrieve order data.';
                }
            } catch (error) {
                errorMessage = 'Network error: Could not connect to the backend.';
                console.log("Error:", error);
            }
        } else {
            errorMessage = 'No ID provided.';
        }
    });
</script>

<h1>Order Tracking for ID: {id}</h1>

{#if $orderData}

{:else if errorMessage}
    <p style="color: red;">{errorMessage}</p>
{:else}
    <p>Waiting for order details...</p>
{/if}

<div class="main2">
    <div class="background2">
        <div class="logo2"></div>
        <div class="order-box-main">
            <div class="title-wrapper">
                <div class="order-number-text">Ordrenummer: #{id}</div>
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
            <div class="total-estimate">Estimeret færdiggørrelse: "totalEstimatedTime" dage</div>
            <div class="order-box-items">
                {#if $orderData}
                    {#if $orderData.length === 0}
                        <p style="color: red">No items found for this order.</p>
                    {:else}
                        {#each $orderData as item}
                            <ItemComponent orderItem={item} name={item.item.name} productType={item.product_type} quantity={item.itemAmount} />
                        {/each}
                    {/if}
                {/if}
            </div>
        </div>
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
        box-sizing: border-box; /* Include padding and border in element's total width and height */
    }

    .main2 {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
        align-items: center;
        gap: 10px;
        /*padding: 0px 2%;*/
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

    .order-box-main {
        align-items: flex-start;
        padding: 15px 20px;
        background-color: #ffffff;
        border-radius: 15px;
        overflow: hidden;
        border: 1px solid;
        border-color: #00000026;
        display: flex;
        flex-direction: column;
        position: relative;
        align-self: stretch;
        width: 100%;
        flex: 0 0 auto;
    }

    .title-wrapper {
        gap: 10px;
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

    .total-estimate {
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-size: 1.0rem;
        font-weight: 400;
        color: rgba(0, 0, 0, 0.8);
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
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