<script lang="ts">
    import { onMount } from "svelte";

    export let dialog: any;
    let name: string = '';
    let selectedStatusId = '';
    let statusDefinitions: { id: number; name: string }[] = [];
    let selectedStatuses: { id: number; name: string }[] = [];

    onMount(async () => {
        await fetchData();
    });

    async function fetchData() {
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
    }

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
                alert('Produktionstype blev oprettet!');

                setTimeout(() => { // To clear the input fields
                    name = '';
                    statusDefinitions = [];
                    selectedStatuses = [];
                    differentSteps = [];
                    selectedStatusId = '';
                }, 100);

                if (dialog) {
                    dialog.close(); // Close the dialog after submission
                }
            } else {
                console.error('Error creating product type:', await response.text());
                alert('Noget gik galt under oprettelse af produktionstypen.');
            }
            await fetchData();
        } catch (error) {
            console.error('Error:', error);
            alert('Noget gik galt under oprettelse af produktionstypen.');
        }
    }
</script>

<div class="dialog-body">
    <form on:submit={handleSubmit}>
        <label for="type-name">Navn på produktionstype:</label>
        <input type="text" id="type-name" bind:value={name} required />

        <label for="statusDefinition">Tilføj produktionstrin:</label>
        <div id="status-select-container">
            <select id="statusDefinition" name="statusDefinition" on:change={(e) => addStatusDefinition(Number(e.target.value))} bind:value={selectedStatusId}>
                <option value="" disabled selected>Vælg produktionstrin</option>
                {#each statusDefinitions as definition}
                    <option value={definition.id}>{definition.name}</option>
                {/each}
            </select>
        </div>

        <div id="selected-statuses-container">
            <h3>Valgte Produktionstrin:</h3>
            {#each selectedStatuses as status, index}
                <div class="selected-status">
                    <span>Trin {index + 1}: {status.name}</span>
                    <button type="button" class="remove-btn" on:click={() => removeStatusDefinition(index)}>Fjern</button>
                </div>
            {/each}
        </div>

        <input type="submit" value="Opret" />
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

    .selected-status .remove-btn {
        background-color: #FF0000;
        color: white;
        border: none;
        padding: 12px;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1rem;
        transition: background-color 0.3s ease;
    }

    .selected-status .remove-btn:hover {
        background-color: #d70000;
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
