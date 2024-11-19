<script lang="ts">
    import {onMount} from "svelte";

    let id: number;
    let customerName: string = '';
    let priority: boolean = false;
    let notes: string = '';
    let items: Map<number, number> = new Map(); // For items being posted

    let selectedItems: Array<{ itemId: number, quantity: number }> = []; // For items being selected
    let itemsArray: any[] = []; // For items being fetched

    onMount(async () => {
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
    });

    async function handleSubmit(event: Event) {
        event.preventDefault(); // Prevent the default form submission behavior

        selectedItems
            .filter(item => item.itemId != 0 && item.quantity != 0)
            .forEach(item => {
                items.set(item.itemId, item.quantity);
            });

        const plainItems = Object.fromEntries(items);

        console.log(items);

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
                alert('Item submitted successfully!');
            } else {
                console.error('Failed to submit the item:', await response.text());
                alert('Failed to submit the item.');
            }
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
        <label for="order-id">ID på ordren</label>
        <input type="number" id="order-id" name="name" bind:value={id} required>

        <label for="costumer-name">Navn på kunde</label>
        <input type="text" id="costumer-name" name="name" bind:value={customerName} required>

        <label for="priority">Prioriteret ordre</label>
        <input type="checkbox" id="costumer-name" name="name" bind:checked={priority}>

        <label for="notes">Valgfrie noter</label>
        <input type="text" id="costumer-name" name="name" bind:value={notes} required>

        <div>
            {#each selectedItems as selectedItem, index}
                <div id="item-and-quantity">
                    <label for="items">Artikel {index + 1}</label>
                    <select id="item" name="name" bind:value={selectedItem.itemId} required>
                        <option value="0" disabled>Ny artikel</option>
                        {#each itemsArray as item}
                            <option value="{item.id}">{item.id} - {item.name}</option>
                        {/each}
                    </select>

                    <input type="number" id="quantity" name="quantity" bind:value={selectedItem.quantity} />
                </div>
            {/each}

            <button type="button" on:click={addNewSelection}>Tilføj en artikel og antal</button>
        </div>

        <input type="submit" value="Submit">
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
    form input[type="submit"],
    form button[type="button"] {
        background-color: #007bff;
        color: white;
        border: none;
        padding: 12px;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1rem;
        transition: background-color 0.3s ease;
    }

    /* Submit and button hover effect */
    form input[type="submit"]:hover,
    form button[type="button"]:hover {
        background-color: #0056b3;
    }

    /* Submit and button active state */
    form input[type="submit"]:active,
    form button[type="button"]:active {
        background-color: #003d80;
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
