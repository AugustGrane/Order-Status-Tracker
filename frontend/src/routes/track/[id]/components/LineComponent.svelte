<script lang="ts">
    export let done:boolean = false;
</script>

{#if done}
    <svg xmlns="http://www.w3.org/2000/svg" width="74" height="2" viewBox="0 0 74 2" fill="none">
        <path d="M0.727264 1.5L73.4545 1.50001" stroke="#828282" stroke-width="2" stroke-linecap="round"/>
    </svg>
{:else}
    <svg xmlns="http://www.w3.org/2000/svg" width="73" height="2" viewBox="0 0 73 2" fill="none">
        <path d="M0.181808 1.5L72.9091 1.50001" stroke="#ABABAB" stroke-width="2" stroke-linecap="round"/>
    </svg>
{/if}