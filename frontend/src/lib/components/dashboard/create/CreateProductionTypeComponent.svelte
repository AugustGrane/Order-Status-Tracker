<script lang="ts">
    import { onMount } from "svelte";

    let name: string = '';
    let statusDefinitions: { id: number; name: string }[] = [];
    let selectedStatuses: { id: number; name: string }[] = [];

    onMount(async () => {
        try {
            const response = await fetch('/api/get-all-status-definitions');
            if (response.ok) {
                statusDefinitions = await response.json();
            } else {
                console.error('Failed to fetch status definitions:', await response.text());
            }
        } catch (error) {
            console.error('Error fetching status definitions:', error);
        }
    });

    function addStatusDefinition(statusDefinitionId: any) {
        const selected = statusDefinitions.find(sd => sd.id === statusDefinitionId);
        if (selected && !selectedStatuses.some(sd => sd.id === statusDefinitionId)) {
            selectedStatuses = [...selectedStatuses, selected];
        }
    }
    function removeStatusDefinition(index: number) {
        selectedStatuses.splice(index, 1);
        selectedStatuses = [...selectedStatuses];
    }

    async function handleSubmit(event: Event) {
        event.preventDefault();
        let differentSteps: number[] = [];

        for(let i: number = 0; i < selectedStatuses.length; i++) {
            differentSteps.push(selectedStatuses[i].id);
        }

        let productTypeDTO = {
            name,
            differentSteps // Map to IDs for the backend
        };
        console.log('DTO being sent:', productTypeDTO);

        try {
            const response = await fetch('/api/create-product-type', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(productTypeDTO),
            });

            if (response.ok) {
                console.log('ProductType created successfully:', productTypeDTO);
                alert('Product type created successfully!');
            } else {
                console.error('Error creating product type:', await response.text());
                alert('Failed to create product type.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while creating the product type.');
        }
    }
</script>

<div class="dialog-body">
    <form on:submit={handleSubmit}>
        <label for="type-name">Navn på produktionstype:</label>
        <input type="text" id="type-name" bind:value={name} required />

        <label for="statusDefinition">Tilføj en status definition:</label>
        <div id="status-select-container">
            <select id="statusDefinition" name="statusDefinition" on:change={(e) => addStatusDefinition(Number(e.target.value))}>
                <option value="" disabled selected>Vælg en status definition</option>
                {#each statusDefinitions as definition}
                    <option value={definition.id}>{definition.name}</option>
                {/each}
            </select>
        </div>

        <div id="selected-statuses-container">
            <h3>Valgte status definitioner:</h3>
            {#each selectedStatuses as status, index}
                <div class="selected-status">
                    <span>{status.name}</span>
                    <button type="button" on:click={() => removeStatusDefinition(index)}>Fjern</button>
                </div>
            {/each}
        </div>

        <input type="submit" value="Opret produkttype" />
    </form>
</div>

<style>
    /* General dialog container styling */
    .dialog-body {
        max-width: 500px;
        margin: 2rem auto;
        padding: 2rem;
        box-sizing: border-box;
        background-color: #f9f9f9;
        border-radius: 8px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    }

    /* Form labels */
    form label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #333;
    }

    /* Form input fields */
    form input[type="text"], form select {
        width: 100%;
        padding: 10px;
        margin-bottom: 20px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
        box-sizing: border-box;
    }

    /* Focus effect on input fields */
    form input[type="text"]:focus, form select:focus {
        border-color: #007bff;
        outline: none;
    }

    /* Submit button */
    form input[type="submit"], form button[type="button"] {
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
    form input[type="submit"]:hover, form button[type="button"]:hover {
        background-color: #0056b3;
    }

    /* Submit and button active state */
    form input[type="submit"]:active, form button[type="button"]:active {
        background-color: #003d80;
    }

    /* Container for dynamic item-and-quantity rows */
    #selected-statuses-container {
        margin-bottom: 20px;
    }

    /* Individual selected status styling */
    .selected-status {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 10px;
        padding: 8px;
        background-color: #e9ecef;
        border-radius: 5px;
    }

    .selected-status button {
        background-color: #dc3545;
        color: white;
        border: none;
        padding: 4px 8px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 0.9rem;
    }

    .selected-status button:hover {
        background-color: #c82333;
    }

    @media (max-width: 600px) {
        .dialog-body {
            padding: 1rem;
        }

        form input[type="text"], form select {
            font-size: 0.9rem;
        }
    }
</style>
