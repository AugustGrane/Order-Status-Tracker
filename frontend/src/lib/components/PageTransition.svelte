<script lang="ts">
    import { fade } from 'svelte/transition';
</script>

<div in:fade={{ duration: 200 }} out:fade={{ duration: 150 }}>
    <slot />
</div>
