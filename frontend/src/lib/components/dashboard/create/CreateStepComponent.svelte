<script lang="ts">
    import {onMount} from "svelte";
    import Dialog from "$lib/components/dialog/Dialog.svelte";
    import ChooseExistingImageComponent from "$lib/components/dashboard/create/ChooseExistingImageComponent.svelte";

    onMount(() => {
    });

    let name: string;
    let description: string;
    let image: File;
    let existingImageDialog: any;

    async function handleSubmit() {

        try {
            const formData = await populateFormData();
            const response = await fetch('api/status-definitions', {
                method: 'POST',
                body: formData,
            });
        } catch (error) {
            console.error('Error:', error);
            alert('Failed to submit data.');
        }
    }

    async function populateFormData() {
        const formData = new FormData();
        formData.append('name', name);
        formData.append('description', description);
        formData.append('image', image);
        for (const [key, value] of formData.entries()) {
            console.log(`${key}:`, value);
        }
        return formData;
    }
    async function showImagePickerDialog(){
        existingImageDialog.showModal();
    }

</script>

<div class="dialog-body">
    <form enctype="multipart/form-data">
        <label for="step-name">Navn på trin</label>
        <input type="text" id="step-name" name="name" bind:value={name} required>

        <label for="step-description">Beskrivelse af trin:  </label>
        <input type="text" id="step-description" name="description" bind:value={description} required>

        <label for="step-image">Upload bilede</label>
        <input type="file" id="step-image" name="image" bind:value={image} required>
        <input type="submit" value="Submit" on:click={populateFormData}>
    </form>
    <button on:click={showImagePickerDialog}>Vælg eksisterende billede </button>
</div>

<Dialog title="Vælg et billede" bind:dialog = {existingImageDialog }>
    <ChooseExistingImageComponent/>
</Dialog>

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




