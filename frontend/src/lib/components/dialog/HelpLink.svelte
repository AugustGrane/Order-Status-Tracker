<script lang="ts">
    import Dialog from './Dialog.svelte';
    let dialog: any;
</script>

<div style="justify-content: center; align-items: center; padding-top: 0.5rem">
    <a
            href="#"
            class="help-link"
            on:click|preventDefault={() => dialog.showModal()}
    >
        How to find order number
        <span class="question-circle">?</span>
    </a>
</div>

<Dialog title="Order Number Required" bind:dialog on:close={() => console.log('closed')}>
    <div class="dialog-body">
        <p>You need to enter your order number. You should be able to find this order number in the email we sent you.</p>
        <p class="note">You can press ESC or click the × button to close</p>
    </div>
</Dialog>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    :root {
        font-size: 16px;
        --font-primary: 'Roboto', Arial, sans-serif;
    }

    .help-link {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        color: #0066cc;
        text-decoration: none;
        cursor: pointer;
        font-family: var(--font-primary);
    }

    .help-link:hover {
        text-decoration: underline;
    }

    .question-circle {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 16px;
        height: 16px;
        border-radius: 50%;
        background-color: #0066cc;
        color: white;
        font-family: var(--font-primary);
        font-weight: bold;
    }
</style>