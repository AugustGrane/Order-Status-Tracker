<script lang="ts">
    import { goto } from '$app/navigation';  // Import goto function
    let inputText = '';
    let errorMessage = '';

    function handleSubmit(event:any) {
        event.preventDefault();

        // Post to database here and check if order exists
        let isValid:boolean = true;

        if (isValid) {
            errorMessage = '';
            goto(`/track/${inputText.trim()}`);
        } else {
            errorMessage = 'Order number not found';
        }
    }
</script>

<form on:submit={handleSubmit}>
    <label for="inputText">Enter Text:</label>
    <input
            id="inputText"
            type="text"
            bind:value={inputText}
            placeholder="Type only numbers here"
            required
    />
    <button type="submit">Submit</button>

    {#if errorMessage}
        <p style="color: red;">{errorMessage}</p>
    {/if}
</form>


