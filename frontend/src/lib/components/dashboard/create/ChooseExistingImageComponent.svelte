<!--<script lang="ts">-->
<!--    import { onMount } from "svelte";-->

<!--    let images: string[] = []; // Initialize an array to store image URLs-->

<!--    onMount(async () => {-->
<!--        try {-->
<!--            const response = await fetch("api/get-images"); // Fetch images from Spring Boot API-->
<!--            if (!response.ok) {-->
<!--                throw new Error(`Failed to fetch images: ${response.statusText}`);-->
<!--            }-->
<!--            images = await response.json(); // Assume the API returns an array of image URLs-->
<!--        } catch (error) {-->
<!--            console.error(error);-->
<!--        }-->
<!--    });-->
<!--</script>-->

<!--<div>-->
<!--    {#if images.length > 0}-->
<!--        {#each images as image}-->
<!--            <img src={image} alt="Image" />-->
<!--        {/each}-->
<!--    {:else}-->
<!--        <p>Loading images...</p>-->
<!--    {/if}-->
<!--</div>-->
