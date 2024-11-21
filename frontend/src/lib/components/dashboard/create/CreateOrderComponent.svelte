<script lang="ts">
    import {onMount} from "svelte";

    let id: number | undefined;
    let customerName: string = '';
    let priority: boolean = false;
    let notes: string = '';
    let items: Map<number, number> = new Map(); // For items being posted

    let selectedItems: Array<{ itemId: number, quantity: number }> = []; // For items being selected
    let itemsArray: any[] = []; // For items being fetched

    onMount(async () => {
        await fetchData();
    });

    async function fetchData() {
        try {
            const response = await fetch("/api/get-all-items");

            if (response.ok) {
                itemsArray = await response.json();
                addNewSelection();
            } else {
                throw new Error('Failed to fetch items');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async function handleSubmit(event: Event) {
        event.preventDefault(); // Prevent the default form submission behavior

        selectedItems
            .filter(item => item.itemId != 0 && item.quantity != 0)
            .forEach(item => {
                items.set(item.itemId, item.quantity);
            });

        const plainItems = Object.fromEntries(items);

        let orderDTO = {
            id,
            customerName,
            priority,
            notes,
            items: plainItems
        }

        try {
            const response = await fetch("/api/create-order", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json', // Specify that we're sending JSON
                },
                body: JSON.stringify(orderDTO) // Convert the object to a JSON string
            });

            if (response.ok) {
                console.log('Form submitted successfully:', orderDTO);
                alert('Ordren blev oprettet!');

                selectedItems = []; // clear both selection array and items array for a new order
                items.clear();
                addNewSelection(); // Initialize the first index again

                setTimeout(() => { // To clear the input fields
                    id = undefined;
                    customerName = '';
                    priority = false;
                    notes = '';
                }, 100);

            } else {
                console.error('Noget gik galt under oprettelse af ordren: ', await response.text());
                alert('Noget gik galt under oprettelse af ordren.');
            }
            await fetchData();
        } catch (error) {
            console.error('Error:', error);
            alert('Failed to submit data.');
        }
    }

    function addNewSelection() {
        selectedItems = [...selectedItems, { itemId: 0, quantity: 0 }]; // For dynamic updating of the selectedItems array
    }

</script>

<div class="dialog-body">
    <form on:submit={handleSubmit}>
        <label for="order-id">ID på ordren:</label>
        <input type="number" id="order-id" name="name" bind:value={id} required>

        <label for="costumer-name">Navn på kunde:</label>
        <input type="text" id="costumer-name" name="name" bind:value={customerName} required>

        <label for="priority">Prioriteret ordre:</label>
        <input type="checkbox" id="costumer-name" name="name" bind:checked={priority}>

        <label for="notes">Noter:</label>
        <input type="text" id="costumer-name" name="name" bind:value={notes}>

        <div>
            {#each selectedItems as selectedItem, index}
                <div id="item-and-quantity">
                    {#if index === 0}
                        <label for="items">Artikel {index + 1}:</label>
                        <select id="item" name="name" bind:value={selectedItem.itemId} required>
                            <option value="0" disabled>Ny artikel</option>
                            {#each itemsArray as item}
                                <option value="{item.id}">{item.id} - {item.name}</option>
                            {/each}
                        </select>
                    {:else}
                        <div class="remove-div">
                            <label for="items">Artikel {index + 1}:</label>
                            <button type="button" class="remove-item" on:click={() => { selectedItems = selectedItems.filter((_, i) => i !== index); }}>
                                Fjern artikel
                            </button>
                        </div>
                        <select id="item" name="name" bind:value={selectedItem.itemId}>
                            <option value="0" disabled>Ny artikel</option>
                            {#each itemsArray as item}
                                <option value="{item.id}">{item.id} - {item.name}</option>
                            {/each}
                        </select>
                    {/if}


                    <input type="number" id="quantity" name="quantity" bind:value={selectedItem.quantity} />
                </div>
            {/each}
            <div class="add-item-div">
                <button type="button" class="add-item" on:click={addNewSelection}>Tilføj en artikel og antal</button>
            </div>
        </div>

        <input type="submit" value="Opret">
    </form>
</div>

<style>
    /* General dialog container styling */
    .dialog-body {
        max-width: 500px;
        margin: 2rem auto;
        padding: 2rem;
        box-sizing: border-box;
    }

    /* Form labels */
    form label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #333;
    }

    .add-item-div {
        margin-top: -20px;
        margin-bottom: 10px;
    }

    /* Form input fields */
    form input[type="number"],
    form input[type="text"],
    form input[type="checkbox"],
    form select {
        width: 100%;
        padding: 10px;
        margin-bottom: 20px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
        box-sizing: border-box; /* Ensures equal padding on all sides */
    }

    /* Adjusting for checkbox input */
    form input[type="checkbox"] {
        width: auto;
        height: 18px; /* Increased height */
        width: 18px; /* Increased width */
        margin-right: 10px;
    }

    /* Focus effect on input fields */
    form input[type="number"]:focus,
    form input[type="text"]:focus,
    form select:focus {
        border-color: #007bff;
        outline: none;
    }

    /* Submit button */
    form input[type="submit"] {
        background-color: #007bff;
        color: white;
        border: none;
        padding: 12px;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1rem;
        transition: background-color 0.3s ease;
    }

    form button.add-item {
        background-color: #69D84F;
        color: white;
        border: none;
        padding: 7px;
        border-radius: 5px;
        cursor: pointer;
        font-size: 0.75rem;
        transition: background-color 0.3s ease;
    }

    form button.remove-item {
        background-color: #FF0000;
        color: white;
        border: none;
        padding: 7px;
        border-radius: 5px;
        cursor: pointer;
        font-size: 0.75rem;
        transition: background-color 0.3s ease;
    }

    .remove-div {
        display: flex;
        justify-content: space-between;
        margin-bottom: -15px;
    }

    form button.remove-item:hover {
        background-color: #d70000;
    }

    form button.add-item:hover {
        background-color: #42b328;
    }

    /* Submit and button hover effect */
    form input[type="submit"]:hover {
        background-color: #0056b3;
    }

    /* Submit and button active state */
    form input[type="submit"]:active,
    form button[type="button"]:active {
        background-color: #90EE90;
    }

    /* Responsive adjustments */
    @media (max-width: 600px) {
        .dialog-body {
            padding: 1rem;
        }

        form input[type="number"],
        form input[type="text"],
        form input[type="submit"],
        form button[type="button"] {
            font-size: 0.9rem;
        }
    }

    /* Container for dynamic item-and-quantity rows */
    #item-and-quantity {
        margin-bottom: 10px;
    }

    /* Dropdown styling */
    form select {
        width: 100%;
        padding: 10px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
        box-sizing: border-box;
    }

    form button[type="button"] {
        margin-bottom: 25px; /* Adds space below Add New Item button */
    }

</style>
