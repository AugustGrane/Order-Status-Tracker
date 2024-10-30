<script lang="ts">
    export let done: boolean = false;
</script>

{#if done}
    <img class="line" alt="line" src="https://c.animaapp.com/rBpIyes1/img/back-line.svg" />
{:else}
    <img class="line" alt="line" src="https://c.animaapp.com/rBpIyes1/img/back-line-1.svg" />
{/if}

<style>
    .line {
        position: relative;
        flex: 1;
    }
</style>