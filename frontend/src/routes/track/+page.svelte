<script lang="ts">
    import { goto } from '$app/navigation';  // Import goto function
    let orderInput = '';
    let errorMessage = '';

    async function handleSubmit(event:any) {
        event.preventDefault();
        // Post to database here and check if order exists
        try {
            const response = await fetch(`http://localhost:8080/endpoints/${orderInput.trim()}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ orderInput: orderInput.trim() })
            });
            // Checks if the respons is ok, if yes it sends the JSON response
            if (response.ok) {
                const data = await response.json();
                // Redirect to /track/{orderInput} on success
                console.log("Sent data:", data);
                goto(`/track/${orderInput.trim()}`);
            } else {
                const errorData = await response.json();
                errorMessage = errorData.message || 'Failed to submit order.';
            }
        } catch (error) {
            errorMessage = 'Network error: Could not connect to the backend.';
        }
    }
</script>

<form on:submit={handleSubmit}>
    <label for="orderInput">Enter Text:</label>
    <input
            id="orderInput"
            type="text"
            bind:value={orderInput}
            placeholder="Type only numbers here"
            required
    />
    <button type="submit">Submit</button>

    {#if errorMessage}
        <p style="color: red;">{errorMessage}</p>
    {/if}
</form>


<!--<div class="main">
    <div class="background">
        <div class="focus-box">
            <div class="logo"></div>
            <div class="title">Se din proces</div>
            <div class="form-wrapper">
                <form on:submit={handleSubmit}>
                    <label for="orderInput">Enter Text:</label>
                    <input
                            id="orderInput"
                            type="text"
                            class="input-field"
                            bind:value={orderInput}
                            placeholder="Type only numbers here"
                            required
                    />
                    <button type="submit" class="button">Spor din ordres proces</button>

                    {#if errorMessage}
                        <p style="color: red;">{errorMessage}</p>
                    {/if}
                </form>
                <form>
                    <input
                            id="orderInput"
                            type="text"
                            placeholder="Indtast ordrenummer"
                            required
                    />
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
</div>-->