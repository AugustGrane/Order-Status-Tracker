<script lang="ts">
    import Line from './LineComponent.svelte';

    export let status: string;
    export let icon: string;
    export let current: boolean = false;
    export let done: boolean = false;
    export let firstItem: boolean = false;

    // Remove 'frontend/static/' from the icon path if it exists
    $: cleanIconPath = icon.replace('frontend/static/', '');
</script>

{#if !firstItem}
    <Line done={!current && done} current={current} />
{/if}

<div class="step">
    <div class="circle-wrapper">
        <div class="icon-wrapper" class:current={current} class:done={!current && done}>
            <div class="icon" style="background: url('/{cleanIconPath}') no-repeat center;"></div>
        </div>
        <div class="status-text">
            {#if current}
                <div class="current-status">{status}</div>
            {:else}
                <div class="status">{status}</div>
            {/if}
        </div>
    </div>
</div>

<style>
    /* Add a Google Font import for Roboto */
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    :root {
        font-size: 16px;
        --font-primary: 'Roboto', Arial, sans-serif;
    }

    *,
    *::before,
    *::after {
        box-sizing: border-box;
    }

    .step {
        display: flex;
        align-items: flex-start;
        justify-content: center;
        gap: 12px;
        padding: 0px 8px;
        position: relative;
        flex: 1;
        flex-grow: 1;
    }

    .circle-wrapper {
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        position: relative;
        flex: 0 0 auto;
    }

    .icon-wrapper {
        width: 50px;
        height: 50px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 50%;
        background-color: #f8f8f8;
        padding: 10px;
        border: 3px solid #dddddd;
        transition: all 0.3s ease;
    }

    .icon-wrapper.current {
        border-color: #1166ee;
        background-color: #f0f7ff;
        box-shadow: 0 0 0 4px rgba(17, 102, 238, 0.1);
    }

    .icon-wrapper.done {
        border-color: #24A1477F;
        background-color: #f0fff4;
    }

    .icon {
        width: 100%;
        height: 100%;
        background-size: contain !important;
        transition: all 0.3s ease;
        filter: grayscale(100%) opacity(0.5);
    }

    .current .icon,
    .done .icon {
        filter: none;
    }

    .status-text {
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        position: relative;
        flex: 0 0 auto;
        margin-top: 4px;
    }

    .current-status {
        color: #000000;
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-weight: 500;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .status {
        color: #808080;
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }
</style>
