<script lang="ts">
    import Dialog from "$lib/components/dialog/Dialog.svelte";
    import ChooseExistingImageComponent from "$lib/components/dashboard/create/ChooseExistingImageComponent.svelte";

    let name: string;
    let description: string;
    let existingImageDialog: any;
    let chosenImage:string = "";

    async function showImagePickerDialog(){
        existingImageDialog.showModal();
    }

    async function refresh(){
        if(name && description){
            window.location.reload();
        }
    }
</script>

<div class="dialog-body">
    <form enctype="multipart/form-data"
          action="?/upload"
          method="post">
        <label for="step-name">Navn på trin:</label>
        <input type="text" id="step-name" name="name" bind:value={name} required>

        <label for="step-description">Beskrivelse af trin:  </label>
        <input type="text" id="step-description" name="description" bind:value={description} required>

        <label for="step-image">Upload billede:</label>
        <div class="submit-div">
            <input type="file" id="step-image" name="image" accept=".png, .jpg">
            <input type="submit" value="Opret" on:click={refresh}>
        </div>

        <!-- Hidden input to store the chosen image URL -->
        <input type="hidden" name="chosenImage" value={chosenImage}>

        <!-- Button to open the image picker -->
        <button type="button" on:click={showImagePickerDialog}>Vælg et eksisterende billede</button>

        <!-- Display the chosen image -->
        {#if chosenImage}
            <div class="chosen-image">
                <h3>Selected Image:</h3>
                <img src={chosenImage} alt="Chosen Image">
            </div>
        {/if}

    </form>
</div>

<Dialog title="Choose an Image" bind:dialog={existingImageDialog}>
    <ChooseExistingImageComponent bind:chosenImage/>
</Dialog>
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

    /* Form inputs */
    form input[type="text"] {
        width: 100%;
        padding: 10px; /* This ensures equal space inside the input field */
        margin-bottom: 20px;
        border-radius: 5px;
        border: 1px solid #ccc;
        font-size: 1rem;
        box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
        transition: border-color 0.3s ease;
        box-sizing: border-box; /* Ensures padding doesn't affect the overall width */
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

    /* Chosen image preview */
    .chosen-image img {
        max-width: 100%;
        height: auto;
        margin-top: 1rem;
        border: 1px solid #ccc;
        border-radius: 4px;
    }

    .submit-div {
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        margin-bottom: -25px;
    }
</style>