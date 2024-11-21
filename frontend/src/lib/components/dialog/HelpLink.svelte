<script lang="ts">
    import Dialog from './Dialog.svelte';
    let dialog: any;
</script>

<div style="justify-content: center; align-items: center; padding-top: 0.5rem">
    <button
            class="help-link"
            on:click={() => dialog.showModal()}
    >
        <u>Hvor finder jeg mit ordrenummer?</u>
        <div class="question-circle"></div>
    </button>
</div>

<Dialog title="Hvor finder jeg mit ordrenummer?" bind:dialog>
    <div class="dialog-body">
        <p>Ordrenummeret kan findes i den ordrebekræftelse du har modtaget fra os på mail.</p>
        <p>Hvis du ønsker yderligere hjælp, kan du kontakte Gtryk på info@gtryk.dk</p>
    </div>
</Dialog>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    :root {
        font-size: 16px;
        --font-primary: 'Roboto', Arial, sans-serif;
    }

    u {
        font-size: 1rem;
    }

    .dialog-body {
        margin: 2rem 0;
        font-family: var(--font-primary);
        font-size: 1.2rem;
    }

    .help-link {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        color: #4d56cd;
        cursor: pointer;
        background: none;
        border: none;
        padding: 0;
    }

    .help-link:hover {
        text-decoration: underline;
    }

    .question-circle {
        background-image: url("/uploads/Question mark icon.png"), linear-gradient(transparent, transparent);
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 16px;
        height: 16px;
        border-radius: 50%;
        background-color: #4d56cd;
        background-repeat: no-repeat;
        background-position: center;
        background-size: 100%;
    }

    @media(max-width: 768px) {
        u {
            font-size: 0.85rem;
        }
        .question-circle {
            width: 12px;
            height: 12px;
        }
        .help-link {
            font-size: 12px;}
    }
</style>