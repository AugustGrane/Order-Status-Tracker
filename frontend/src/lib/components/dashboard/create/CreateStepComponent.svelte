<script lang="ts">
    import {onMount} from "svelte";

    onMount(() => {
    });

    let name: String;
    let description: String;
    let image: String;

    async function handleSubmit() {
        let newProductionStep = {
            name,
            description,
            image
        }

        try {
            console.log(newProductionStep);
            const response = await fetch('api/status-definitions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newProductionStep)
            });
        } catch (error) {
            console.error('Error:', error);
            alert('Failed to submit data.');
        }
    }

</script>

<div class="dialog-body">
    <form>
        <label for="step-name">Navn på trin</label>
        <input type="text" id="step-name" name="name" bind:value={name} required>

        <label for="step-description">Beskrivelse af trin</label>
        <input type="text" id="step-description" name="description" bind:value={description} required>

        <label for="step-image">Billede</label>
        <input type="text" id="step-image" name="image" bind:value={image} required>
        <input type="submit" value="Submit" on:click={handleSubmit}>
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

    /* Form inputs */
    form input[type="text"] {
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
    form input[type="text"]:focus {
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
        form input[type="submit"] {
            font-size: 0.9rem;
        }
    }
</style>




