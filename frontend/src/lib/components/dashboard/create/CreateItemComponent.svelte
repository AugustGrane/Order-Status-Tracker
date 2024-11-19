<script lang="ts">
    import { onMount } from "svelte";


    let name: string = '';
    let id: number;
    let productTypes: any[] = []; // Declare it as an array of strings
    let productTypeId: number; // Assuming the selected product type is a string


    onMount(async () => {
        try {
            const response = await fetch("/api/get-all-producttypes");

            if (response.ok) {
                productTypes = await response.json();
            } else {
                throw new Error('Failed to fetch product types');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });

    async function handleSubmit(event: Event) {
        event.preventDefault(); // Prevent the default form submission behavior
        let itemDTO = {
            name,
            id,
            productTypeId,
        }

        try {
            const response = await fetch("/api/create-item", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json', // Specify that we're sending JSON
                },
                body: JSON.stringify(itemDTO) // Convert the object to a JSON string
            });

            if (response.ok) {
                console.log('Form submitted successfully:', itemDTO);
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
</script>

<div class="dialog-body">
    <form on:submit={handleSubmit}>
        <label for="item-name">Navn på artikel</label>
        <input type="text" id="item-name" name="name" bind:value={name} required>

        <label for="item-id">ID på artikel</label>
        <input type="number" id="item-id" name="name" bind:value={id} required>

        <label for="productType">Vælg produktionstype</label>
        <select id="productType" name="productType" bind:value={productTypeId} required>
            {#each productTypes as type}
                <option value={type.id}>{type.name}</option>
            {/each}
        </select>

        <input type="submit" value="Submit">
    </form>
</div>

<style>
    /* General dialog container styling */
    .dialog-body {
        max-width: 500px;
        margin: 2rem auto;
        padding: 2rem;
    }

    /* Form labels */
    form label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #333;
    }

    /* Form input fields */
    form input[id="item-name"],
    form select {
        width: 95%;
        padding: 10px;
        margin-bottom: 20px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
    }

    form input[id="item-id"],
    form select {
        width: 95%;
        padding: 10px;
        margin-bottom: 20px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
    }

    form input[id="productType"],
    form select {
        width: 100%;
        padding: 10px;
        margin-bottom: 20px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
    }

    /* Focus effect on input fields */
    form input[type="text"]:focus,
    form select:focus {
        border-color: #007bff;
        outline: none;
    }

    form input[type="number"]:focus,
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

    /* Submit button hover effect */
    form input[type="submit"]:hover {
        background-color: #0056b3;
    }

    /* Submit button active state */
    form input[type="submit"]:active {
        background-color: #003d80;
    }

    /* Responsive adjustments */
    @media (max-width: 600px) {
        .dialog-body {
            padding: 1rem;
        }

        form input[type="text"],
        form select,
        form input[type="submit"] {
            font-size: 0.9rem;
        }
    }

</style>
