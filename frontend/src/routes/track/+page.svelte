<script lang="ts">
    import { goto } from '$app/navigation';
    import { orderStore } from '$lib/stores/orderStore';
    import HelpLink from "$lib/components/dialog/HelpLink.svelte";
    let orderInput = '';
    let errorMessage = '';

    async function handleSubmit(event: Event) {
        event.preventDefault();
        const trimmedOrderId = orderInput.trim();
        
        try {
            // Try to get the order through our store
            await orderStore.getOrder(trimmedOrderId);
            errorMessage = '';
            goto(`/track/${trimmedOrderId}`);
        } catch (error) {
            errorMessage = 'Ordrenummer findes ikke';
            orderInput = ''; // Clear the input field
        }
    }
</script>

<div class="main">
    <div class="background">
        <div class="focus-box">
            <div class="logo"></div>
            <div class="title">Se din proces</div>
            <div class="form-wrapper">
                <form on:submit={handleSubmit}>
                    <input
                            id="orderInput"
                            type="text"
                            class="input-field"
                            bind:value={orderInput}
                            placeholder="Indtast ordrenummer"
                            required
                    />
                    <button type="submit" class="button">Spor din ordres proces</button>

                    {#if errorMessage}
                        <p class="error-message">{errorMessage}</p>
                    {/if}
                </form>
                <HelpLink></HelpLink>


            </div>
            <div class="icon-boxes">
                <div class="icon acceptance"></div>
                <div class="icon print"></div>
                <div class="icon drying"></div>
                <div class="icon shipping"></div>
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

    .button {
        display: block;
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
    }

    .error-message {
        color: #dc3545;
        text-align: center;
        margin-top: 1rem;
        font-family: var(--font-primary);
        font-size: 1rem;
    }

    /* Icon Boxes Grid Layout */
    .icon-boxes {
        display: grid;
        gap: 1.5vw;
        width: 90%;
        max-width: 800px;
        grid-template-columns: repeat(4, 1fr);
        margin-top: 5%;
    }

    .icon {
        width: 100%;
        aspect-ratio: 1;
        background-size: contain;
        background-repeat: no-repeat;
        background-position: center;
        border-radius: 0.25rem;
    }

    .acceptance {
        background-image: url('/Acceptance agreement.png');
    }

    .print {
        background-image: url('/Print icon.png');
    }

    .drying {
        background-image: url('/Drying icon.png');
    }

    .shipping {
        background-image: url('/Shipping icon.png');
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

        .icon-boxes {
            grid-template-columns: repeat(2, 1fr);
        }

        .icon {
            width: 100%;
            height: auto;
        }
    }
</style>
