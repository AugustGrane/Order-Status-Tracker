<script lang="ts">
    import { onMount } from "svelte";

    let filesGet: string[] = [];
    let errorGet: string | null = null;


    // Get all the step images
    onMount(async () => {
        try {
            // Fetch the list of uploaded files
            const response = await fetch('/about'); // Adjust the URL if necessary
            const data = await response.json();

            if (data.success) {
                filesGet = data.files.map((file: string) => `/uploads/${file}`); // Generate full URLs
            } else {
                errorGet = data.error || 'Failed to fetch files.';
            }
        } catch (err) {
            errorGet = 'An unexpected error occurred.';
        }
    });
</script>

{#if errorGet}
    <div class="error">Error: {errorGet}</div>
{:else}
    <div>
        <h2>Uploaded Images</h2>
        <div class="image-grid">
            {#each filesGet as file}
                <img src={file} alt="Uploaded file" loading="lazy" />
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