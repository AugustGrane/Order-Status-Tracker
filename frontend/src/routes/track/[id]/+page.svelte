<script lang="ts">
    import { page } from '$app/stores'; // For accessing the page store
    import { onMount } from 'svelte'; // Importing onMount

    // Declare a variable to hold the ID
    let id;
    // ANY TYPES NEED DEFINING WHEN WE KNOW WHAT TYPE RESPONSE IS
    let orderData:any; // To store fetched order data
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
                } else {
                    errorMessage = 'Failed to retrieve order data.';
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