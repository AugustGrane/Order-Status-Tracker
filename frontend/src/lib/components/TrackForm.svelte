<script lang="ts">
    import { goto } from '$app/navigation';
    import { onMount } from 'svelte';
    import HelpLink from "$lib/components/dialog/HelpLink.svelte";
    
    export let initialValue = '';
    export let initialError = '';
    
    let orderInput = '';
    let errorMessage = '';
    let isLoading = false;

    onMount(() => {
        orderInput = initialValue;
        errorMessage = initialError;
    });

    // Update error message when initialError changes
    $: {
        if (initialError) {
            errorMessage = initialError;
            isLoading = false;
        }
    }

    async function handleSubmit(event: Event) {
        event.preventDefault();
        const trimmedOrderId = orderInput.trim();
        
        // Basic format validation
        if (trimmedOrderId.length < 1) {
            errorMessage = 'Indtast venligst et ordrenummer';
            return;
        }

        // Validate that order number only contains numbers
        if (!/^\d+$/.test(trimmedOrderId)) {
            errorMessage = 'Ordrenummeret må kun indeholde tal';
            return;
        }

        // Show loading state
        isLoading = true;

        try {
            // Navigate to order page
            await goto(`/track/${trimmedOrderId}`);
        } catch (error) {
            errorMessage = 'Der opstod en fejl. Prøv igen.';
        } finally {
            isLoading = false;
        }
    }
</script>

<div class="main">
    <div class="background">
        <div class="focus-box">
            <div class="logo"></div>
            <div class="title">Se status på din ordre</div>
            <div class="form-wrapper">
                <form on:submit={handleSubmit}>
                    <input
                            id="orderInput"
                            type="text"
                            class="input-field"
                            bind:value={orderInput}
                            placeholder="Indtast ordrenummer"
                            required
                            disabled={isLoading}
                    />
                    <button type="submit" class="button" disabled={isLoading}>
                        {#if isLoading}
                            <div class="loading-spinner"></div>
                            <span>Søger...</span>
                        {:else}
                            Find ordre
                        {/if}
                    </button>

                    {#if errorMessage}
                        <p class="error-message">{errorMessage}</p>
                    {/if}
                </form>
                <HelpLink></HelpLink>
            </div>
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

    /* Main container styles */
    .main {
        display: flex;
        width: 100vw;
        height: 100vh;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: #FFF;
        padding: 0;
        margin: 0;
    }

    .background {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        align-self: stretch;
        background-image: url("https://acctcdn.msauth.net/images/AppBackgrounds/49-small_v2_5YqvyYBhSpzXeWvqe16o8A2.jpg");
        background-repeat: no-repeat;
        background-size: cover;
    }

    .focus-box {
        display: flex;
        width: 93.5%;
        max-width: 660px;
        min-width: 385px;
        padding: 3.3vw;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        gap: 1.65vw;
        border-radius: 1.5vw;
        background: #FDFDFD;
    }

    .logo {
        width: 12rem;
        height: 7rem;
        background: url('/gtryk_logo.png') no-repeat center;
        background-size: contain;
    }

    .title {
        color: #000;
        text-align: center;
        font-family: var(--font-primary);
        font-size: 2.5rem;
        font-weight: 700;
    }

    *,
    *::before,
    *::after {
        box-sizing: border-box;
    }

    .form-wrapper {
        display: flex;
        flex-direction: column;
        width: 100%;
        padding: 0;
    }

    .input-field {
        display: block;
        width: 100%;
        padding: 1.5vw;
        border-radius: 0.5vw;
        border: 1px solid #ccc;
        font-family: var(--font-primary);
        font-size: 1.5rem;
        color: #363636;
        margin-bottom: 1rem;
    }

    .input-field:disabled {
        background-color: #f5f5f5;
        cursor: not-allowed;
    }

    .button {
        display: flex;
        width: 100%;
        padding: 1.5vw;
        border-radius: 0.5vw;
        background: #454545;
        color: #FFF;
        font-family: var(--font-primary);
        font-size: 1.5rem;
        font-weight: 400;
        border: none;
        cursor: pointer;
        justify-content: center;
        align-items: center;
        gap: 10px;
        transition: opacity 0.2s;
    }

    .button:disabled {
        opacity: 0.7;
        cursor: not-allowed;
    }

    /* Loading spinner */
    .loading-spinner {
        width: 20px;
        height: 20px;
        border: 3px solid #ffffff;
        border-top: 3px solid transparent;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }

    .error-message {
        color: #dc3545;
        text-align: center;
        margin-top: 1rem;
        font-family: var(--font-primary);
        font-size: 1rem;
    }


    /* Responsive adjustments */
    @media (max-width: 768px) {
        .title {
            font-size: 2rem;
        }

        .focus-box {
            width: 90%;
            padding: 2.2vw;
        }

        .input-field, .button {
            width: 100%;
        }
    }
</style>
