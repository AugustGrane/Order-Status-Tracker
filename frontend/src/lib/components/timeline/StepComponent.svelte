<script lang="ts">
    import Line from './LineComponent.svelte';
    import type {OrderDetailsWithStatus, StatusDefinition} from "$lib/types";
    import { onMount } from 'svelte';

    export let item: OrderDetailsWithStatus;
    export let step: StatusDefinition;
    export let index: number;
    export let status: string;
    export let icon: string;
    export let current: boolean = false;
    export let done: boolean = false;
    export let firstItem: boolean = false;
    let toggleTooltip: boolean = false;

    $: cleanIconPath = icon.replace('frontend/static/uploads/', '');

    onMount(() => {
        toggleTooltip = window.innerWidth <= 1000;
        if (toggleTooltip) {
            document.querySelectorAll('.updated-text').forEach((element) => {
                element.setAttribute('hidden', 'false');
            });
            document.querySelectorAll('.updated').forEach((element) => {
                element.setAttribute('style', 'color: #ffffff');
            });
        }

        window.addEventListener('resize', () => {
            toggleTooltip = window.innerWidth <= 1000;
            if (toggleTooltip) {
                document.querySelectorAll('.updated-text').forEach((element) => {
                    element.setAttribute('hidden', 'false');
                });
            } else {
                document.querySelectorAll('.updated-text').forEach((element) => {
                    element.setAttribute('hidden', 'true');
                });
            }
        });
    });

    const date = new Date(item.updated[step.id]);
    let formattedDate = `${String(date.getDate()).padStart(2, '0')}/${String(date.getMonth() + 1).padStart(2, '0')}`;
    let time = `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
</script>

{#if !firstItem}
    <Line done={!current && done} current={current} />
{/if}

<div class="step" class:tooltip-container={toggleTooltip}>
    <div class="circle-wrapper">
        <div class="icon-wrapper" class:current={current} class:done={!current && done}>
            <div class="icon" style="background: url('/{cleanIconPath}') no-repeat center;"></div>
            <!-- Desktop tooltip -->
            <div class="hello-world-tooltip">
                {step.description}
                <div class="hello-world-tooltip-arrow"></div>
            </div>
        </div>
        <div class="status-text">
            {#if current}
                <div class="current-status">{status}</div>
            {:else}
                <div class="status">{status}</div>
            {/if}
            {#if index <= item.currentStepIndex}
                {#if current}
                    <div class="updated" class:tooltip-text={toggleTooltip} style="color: #000000">
                        <div hidden class="updated-text">Updated:&nbsp;</div>
                        <div>{formattedDate} -</div>&nbsp;
                        <div>kl. {time}</div>
                    </div>
                {:else}
                    <div class="updated" class:tooltip-text={toggleTooltip} style="color: #808080">
                        <div hidden class="updated-text">Updated:&nbsp;</div>
                        <div>{formattedDate} -</div>&nbsp;
                        <div>kl. {time}</div>
                    </div>
                {/if}
            {/if}
        </div>
    </div>
</div>

<style>
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
        padding: 0px 4px;
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
        position: relative;
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

    .updated {
        display: inline-flex;
        align-items: center;
        font-family: var(--font-primary);
        font-weight: 500;
        font-size: 0.8rem;
        line-height: normal;
        padding: 4px 8px;
        white-space: nowrap;
    }

    /* Tooltip container */
    .tooltip-container {
        position: relative;
        display: inline-block;
        cursor: pointer;
    }

    /* Tooltip text */
    .tooltip-container .tooltip-text {
        visibility: hidden;
        width: 160px;
        background-color: #333;
        color: #fff;
        text-align: center;
        border-radius: 4px;
        padding: 8px;
        position: absolute;
        z-index: 1;
        bottom: 125%;
        left: 50%;
        transform: translateX(-50%);
        opacity: 0;
        transition: opacity 0.3s ease;
    }

    /* Tooltip arrow */
    .tooltip-container .tooltip-text::after {
        content: "";
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        border-width: 5px;
        border-style: solid;
        border-color: #333 transparent transparent transparent;
    }

    /* Show tooltip on hover */
    .tooltip-container:hover .tooltip-text {
        visibility: visible;
        opacity: 1;
    }

    /* Hello World tooltip */
    .hello-world-tooltip {
        visibility: hidden;
        width: 200px; /* Increased from 100px to 200px */
        min-width: 100px; /* Added to ensure tooltip is at least as wide as its content */
        max-width: 300px; /* Added maximum width to prevent extremely long tooltips */
        background-color: #333;
        color: #fff;
        font-family: var(--font-primary);
        text-align: center;
        border-radius: 4px;
        padding: 8px;
        position: absolute;
        z-index: 1;
        top: 120%;
        left: 50%;
        transform: translateX(-50%);
        opacity: 0;
        transition: opacity 0.3s ease;
        white-space: normal; /* Allow text to wrap */
        font-size: 0.9rem; /* Added for better readability */
        line-height: 1.4; /* Added for better readability */
    }

    .hello-world-tooltip-arrow {
        content: "";
        position: absolute;
        bottom: 100%;
        left: 50%;
        transform: translateX(-50%);
        border-width: 5px;
        border-style: solid;
        border-color: transparent transparent #333 transparent;
    }


    /* Show Hello World tooltip on hover for desktop */
    @media (min-width: 1001px) {
        .icon-wrapper:hover .hello-world-tooltip {
            visibility: visible;
            opacity: 1;
        }
    }

    @media (max-width: 1000px) {
        .icon-wrapper {
            width: 30px;
            height: 30px;
            padding: 2px;
        }
        .status {
            white-space: normal;
            max-width: 50px;
            font-size: 0.6rem;
        }
        .current-status {
            white-space: normal;
            max-width: 50px;
            font-size: 0.6rem;
        }
        .step {
            min-width: 50px;
        }
        .updated {
            font-size: 0.6rem;
            white-space: normal;
            max-width: 50px;
            flex-wrap: wrap;
            color: #ffffff !important;
        }
        .hello-world-tooltip {
            display: none;
        }
    }
</style>
