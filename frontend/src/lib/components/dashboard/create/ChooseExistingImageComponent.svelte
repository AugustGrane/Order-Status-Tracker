
<script lang="ts">
    import { onMount } from "svelte";

    export let chosenImage: string = "";

    let allStepImageNames: string[] = [];
    let error: string | null = null;

    // Get all the step images
    onMount(async () => {
        await fetchData();
    });

    async function fetchData() {
        try {
            // Fetch the list of uploaded files
            const response = await fetch('/dashboard'); // Adjust the URL if necessary
            const data = await response.json();

            if (data.success) {
                allStepImageNames = data.files.map((file: string) => `/uploads/${file}`); // Generate full URLs
            } else {
                error = data.error || 'Failed to fetch files.';
            }
        } catch (err) {
            error = 'An unexpected error occurred.';
        }
    }

    function  handleImageClick(image: string){
        chosenImage = image;
        console.log(chosenImage);
        fetchData();
    }

</script>

{#if error}
    <div class="error">Error: {error}</div>
{:else}
    <div>
        <h2>Uploaded Images</h2>
        <div class="image-grid">
            {#each allStepImageNames as image}
                <button on:click={() => handleImageClick(image)} class="image-button">
                    <img src={image} alt="Uploaded file" loading="lazy" />
                </button>
            {/each}
        </div>
    </div>
{/if}

<style>
    .image-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
        gap: 10px;
    }

    img {
        width: 100%;
        height: auto;
        object-fit: cover;
        border: 1px solid #ccc;
        border-radius: 4px;
    }
</style>
