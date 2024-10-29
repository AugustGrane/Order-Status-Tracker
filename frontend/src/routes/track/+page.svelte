<script lang="ts">
    import { goto } from '$app/navigation';  // Import goto function
    let orderInput = '';
    let errorMessage = '';

    async function handleSubmit(event:any) {
        event.preventDefault();
        goto(`/track/${orderInput.trim()}`);
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
                        <p style="color: red;">{errorMessage}</p>
                    {/if}
                </form>
            </div>
            <div class="icon-boxes">
                <div class="icon"></div>
                <div class="icon"></div>
                <div class="icon"></div>
                <div class="icon"></div>
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
        /*background-color: #D9D9D94C;*/  /* Background color if no image */
        background-image: url("https://acctcdn.msauth.net/images/AppBackgrounds/49-small_v2_5YqvyYBhSpzXeWvqe16o8A2.jpg");
        background-repeat: no-repeat;
        background-size: cover;
    }

    .focus-box {
        display: flex;
        width: 93.5%; /* Increased width by 10% */
        max-width: 660px; /* Increased max-width by 10% */
        min-width: 385px; /* Increased min-width by 10% */
        padding: 3.3vw; /* Slight increase in padding */
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
        background: url(gtryk_logo.png) no-repeat center;
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
        box-sizing: border-box; /* Include padding and border in element's total width and height */
    }

    .form-wrapper {
        display: flex;
        flex-direction: column;
        width: 100%; /* Ensure it takes the full width of the focus-box */
        padding: 0; /* Ensure no padding affects width */
    }

    /* Styles for input field */
    .input-field {
        display: block;
        width: 100%; /* Full width of the form-wrapper */
        padding: 1.5vw; /* Same padding as before */
        border-radius: 0.5vw;
        border: 1px solid #ccc; /* Border for the input */
        font-family: var(--font-primary);
        font-size: 1.5rem;
        color: #363636;
        margin-bottom: 1rem; /* Small gap between input and button */
    }

    /* Styles for the button */
    .button {
        display: block;
        width: 100%; /* Full width of the form-wrapper */
        padding: 1.5vw; /* Same padding as before */
        border-radius: 0.5vw;
        background: #454545; /* Same background as old .div-wrapper */
        color: #FFF; /* White text color */
        font-family: var(--font-primary);
        font-size: 1.5rem;
        font-weight: 400;
        border: none; /* Remove default button border */
        cursor: pointer; /* Change cursor to pointer */
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
        aspect-ratio: 2 / 1;
        background: #D9D9D9;
        border-radius: 0.25rem;
    }

    /* Responsive adjustments */
    @media (max-width: 768px) {
        .title {
            font-size: 2rem;
        }

        .focus-box {
            width: 90%;
            padding: 2.2vw; /* Adjusted for smaller screens */
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