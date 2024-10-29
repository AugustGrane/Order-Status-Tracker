<script lang="ts">
    import { page } from '$app/stores'; // For accessing the page store
    import { onMount } from 'svelte'; // Importing onMount
    import ItemComponent from './components/ItemComponent.svelte'; // Import the Item component

    // Declare a variable to hold the ID
    let id:any;
    // ANY TYPES NEED DEFINING WHEN WE KNOW WHAT TYPE RESPONSE IS
    let orderData:any; // To store fetched order data
    let orderData2:any;
    let orderData3:any;
    let errorMessage:any; // To hold any error messages

    // Reactive statement to access the ID from the URL
    $: id = $page.params.id;

    // Use onMount to fetch order details based on the ID
    onMount(async () => {
        console.log("Fetching order details for ID:", id);// Log the ID

        if (id) { // Only fetch if id is present
            try {
                const response = await fetch(`http://localhost:8080/endpoints/${id}`, {
                    method: 'POST', // Set the method to POST to match the backend's expectation
                    headers: {
                        'Content-Type': 'application/json' // Optional, depending on backend requirements
                    }
                }); // Adjust the URL accordingly
                if (response.ok) {
                    orderData = await response.json();
                    console.log(`Order Data: ${JSON.stringify(orderData, null, 2)}`);
                } else {
                    errorMessage = 'Failed to retrieve order data.';
                }

                const response2 = await fetch(`http://localhost:8080/api/orders/1/product-types`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (response2.ok) {
                    orderData2 = await response2.json();
                    console.log(`Order Data 2: ${JSON.stringify(orderData2, null, 2)}`)
                } else {
                    errorMessage = 'Failed to retrieve order data 2.';
                }

                const response3 = await fetch(`http://localhost:8080/api/orders/1/product-types`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (response3.ok) {
                    orderData3 = await response3.json();
                    console.log(`Order Data 2: ${JSON.stringify(orderData3, null, 2)}`)
                } else {
                    errorMessage = 'Failed to retrieve order data 3.';
                }
            } catch (error) {
                errorMessage = 'Network error: Could not connect to the backend.';
            }
        } else {
            errorMessage = 'No ID provided.';
        }
    });
</script>

<h1>Order Tracking for ID: {id}</h1>

{#if orderData}
    <p>Tracking details for order ID: {id}</p>
    <pre>{JSON.stringify(orderData, null, 2)}</pre> <!-- Display order details -->
{:else if errorMessage}
    <p style="color: red;">{errorMessage}</p>
{:else}
    <p>Loading order details...</p>
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
                <ItemComponent productType="T-shirt" id="22324" quantity="25" />
                <ItemComponent productType="Banner" id="22325" quantity="10" />
                <ItemComponent productType="Folie" id="22326" quantity="2" />
            </div>
        </div>
    </div>
</div>