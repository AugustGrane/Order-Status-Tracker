<script lang="ts">
    import Line from './LineComponent.svelte';

    export let status:string;
    export let estimate:string;
    export let current:boolean = false;
    export let done:boolean = false;
    export let firstItem:boolean = false;
</script>

{#if !firstItem}
    <Line done="{done}" />
{/if}

<div class="step">
    <div class="circle-wrapper">
        {#if current}
            <div class="current-circle"></div>
        {:else}
            {#if done}
                <div class="circle"></div>
            {:else}
                <div class="circle-2"></div>
            {/if}
        {/if}
        <div class="status-text">
            {#if current}
                <div class="current-status">{status}</div>
                <div class="current-estimate">
                    {#if estimate}
                        ({estimate})
                    {:else}

                    {/if}
                </div>
            {:else}
                <div class="status">{status}</div>
                <div class="estimate">
                    {#if estimate}
                        ({estimate})
                    {:else}

                    {/if}
                </div>
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

    .step {
        display: flex;
        align-items: flex-start;
        justify-content: center;
        gap: 5px;
        padding: 0px 1px;
        position: relative;
        flex: 1;
        flex-grow: 1;
    }

    .circle-wrapper {
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        gap: 5px;
        position: relative;
        flex: 0 0 auto;
    }

    .circle {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #24a147;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .current-circle {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #1166ee;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .circle-2 {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #aaaaaa;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .status-text {
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        position: relative;
        flex: 0 0 auto;
    }

    .current-status {
        color: #000000;
        position: relative;
        width: fit-content;
        margin-top: -1.00px;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .current-estimate {
        color: #000000;
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

    .status {
        color: #808080;
        position: relative;
        width: fit-content;
        margin-top: -1.00px;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .estimate {
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