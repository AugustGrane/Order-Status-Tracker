<script lang="ts">
    export let dialog: any;
    export let title: string;

    function handleClose() {
        if (dialog && dialog.open) {
            dialog.close();
        }
    }

    function handleDialogClose() {
        // console.log("Dialog closed");
    }
</script>

<dialog
        bind:this={dialog}
        on:close={handleDialogClose}
>
    <div class="dialog-content">
        <div class="dialog-header">
            <h2 class="dialog-title">{title}</h2>
            <button
                    type="button"
                    class="close-button"
                    on:click={handleClose}
                    aria-label="Close dialog"
            >×</button>
        </div>
        <slot/>
    </div>
</dialog>

<style>
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    :root {
        font-size: 16px;
        --font-primary: 'Roboto', Arial, sans-serif;
    }

    dialog {
        border: none;
        border-radius: 8px;
        padding: 0;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        max-width: 500px;
        width: 90%;
    }

    dialog::backdrop {
        background: rgba(0, 0, 0, 0.5);
    }

    .dialog-content {
        padding: 20px;
        line-height: 1.5;
        font-family: var(--font-primary);
    }

    .dialog-header {
        display: flex;
        justify-content: center;
        align-items: center;
        position: relative;
        margin-bottom: 16px;
    }

    h2 {
        margin-block-start: 0;
        margin-block-end: 0;
    }

    .dialog-title {
        flex-grow: 1;
        text-align: center;
    }

    .close-button {
        position: absolute;
        right: 0;
        top: 0;
        background: none;
        border: none;
        font-size: 24px;
        cursor: pointer;
        padding: 4px 8px;
        color: #666;
        border-radius: 4px;
        transition: background-color 0.2s;
    }

    .close-button:hover {
        background-color: #f0f0f0;
        color: #333;
    }
</style>