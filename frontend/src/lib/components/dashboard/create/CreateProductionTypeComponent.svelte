<script lang="ts">

    import {onMount} from "svelte";

    onMount(() => {
    });
    let name: string;
    let differentSteps: number[];
    let stepPickerDialog: any;

    
    async function handleSubmit() {

        try {
            const response = await fetch('/api/product-types', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({name, differentSteps: []})
            });
        } catch (error) {
            console.error('Error:', error);
            alert('Failed to submit data.');
        }
    }

    async function showStepPickerDialog(){
        stepPickerDialog.showModal();
    }

</script>


<div class="dialog-body">
    <form>
        <label for="step-name">Navn på trin</label>
        <input type="text" id="production-type-name" name="name" bind:value={name} required>

    </form>
    <button on:click={stepPickerDialog}>Vælg eksisterende billede </button>

    <input type="submit" value="Submit" on:click={handleSubmit}>

</div>