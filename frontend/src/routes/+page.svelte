<script>
    import { onMount } from 'svelte';
    let message = 'Loading...';
    onMount(async () => {
        try {
            const response = await fetch('http://localhost:8080/api/hello');
            const data = await response.text();
            message = data;
        } catch (error) {
            message = 'Error loading message';
            console.error('Error:', error);
        }
    });
</script>
<h1>Hello {message}</h1>