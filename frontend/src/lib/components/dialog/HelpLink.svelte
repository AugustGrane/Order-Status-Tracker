<script lang="ts">
    import Dialog from './Dialog.svelte';
    let dialog: any;
</script>

<div style="justify-content: center; align-items: center; padding-top: 0.5rem">
    <button
            class="help-link"
            on:click={() => dialog.showModal()}
    >
        Hvor finder jeg mit ordrenummer?
        <span class="question-circle">?</span>
    </button>
</div>

<Dialog title="Hvor finder jeg mit ordrenummer?" bind:dialog on:close={() => console.log('closed')}>
    <div class="dialog-body">
        <p>Ordrenummeret kan findes i den ordrebekræftelse du har modtaget fra os på mail.</p>
        <p class="note">Tryk ESC eller × for at lukke dette vindue.</p>
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
        background: none;
        border: none;
        padding: 0;
        font: inherit;
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
