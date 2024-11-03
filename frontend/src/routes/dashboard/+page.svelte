<script lang="ts">
    interface OrderItem {
        id: number;
        // Add other properties as needed
    }

    // export let data: { order: OrderDetailsWithStatus[] };
    let data: OrderItem[] = [];
    let updatedData: OrderItem[] = [];

    async function fetchData() {
        try {
            const response = await fetch('http://localhost:8080/api/orders');
            data = await response.json();
            data.sort((a: OrderItem, b: OrderItem) => a.id - b.id);
            console.log("Response: ", data);
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    }

    async function nextStep(item: number) {
        try {
            const response = await fetch(`http://localhost:8080/api/orders/${item}/next`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                await fetchData();
            }
        } catch (error) {
            console.error("Error updating status:", error);
        }
    }

    async function prevStep(item: number) {
        try {
            const response = await fetch(`http://localhost:8080/api/orders/${item}/previous`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                await fetchData();
            }
        } catch (error) {
            console.error("Error updating status:", error);
        }
    }

    // Fetch data when component mounts
    fetchData();
</script>

{#each data as item (item.id)}
    <div>
        <h2>Order {item.id}</h2>
        <!-- Add other order details here -->
    </div>
    <button on:click={() => prevStep(item.id)}>Move to previous step</button>
    <button on:click={() => nextStep(item.id)}>Move to next step</button>
    <br>
{/each}
