<script lang="ts">
    export let orderId: number;

    async function deleteOrder(event: MouseEvent) {
        event.stopPropagation();
        if (!confirm('Er du sikker på, at du vil slette denne ordre?')) {
            return;
        }

        try {
            const response = await fetch(`/api/delete-order/${orderId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`Fejl ved sletning af ordre med ID ${orderId}: ${response.statusText}`);
            }

            console.log('Sletning succesfuld');
            alert('Ordren blev slettet!');

        } catch (error) {
            console.error('Fejl ved sletning:', error);
            alert('Kunne ikke slette ordren. Prøv igen.');
        }
    }
</script>
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<button class="delete-button" on:click={deleteOrder}><i class="fa fa-trash"></i></button>

<style>
    .delete-button {
        background-color: white;
        color: #dd0000;
        border: none;
        padding: 0.5rem 1rem;
        border-radius: 0.25rem;
        cursor: pointer;
        font-size: 1.25rem;
        transition: background-color 0.2s;
    }

    .delete-button:hover {
        background-color: #ffcccc;
    }
</style>