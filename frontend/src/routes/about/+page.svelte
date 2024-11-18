<script lang="ts">
    import CreateComponent from "$lib/components/dashboard/create/CreateComponent.svelte";

    let count = 0;
</script>

<CreateComponent></CreateComponent>

<h1>About</h1>
<p>The count is {count}</p>
<button on:click={() => count += 1}>Click me</button>
