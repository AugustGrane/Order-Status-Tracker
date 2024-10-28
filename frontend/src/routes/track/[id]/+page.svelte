<script>
    import { page } from '$app/stores';

    // Extract the 'id' parameter from the URL
    $: id = $page.params.id;

</script>

<p>Hello: {id}</p>
