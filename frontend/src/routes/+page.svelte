<script>
    import { onMount } from 'svelte';
    import {goto} from "$app/navigation";
    let message = 'Loading...';
    onMount( () => {
        goto("/track");
    });
</script>
<h1>Hello {message}</h1>