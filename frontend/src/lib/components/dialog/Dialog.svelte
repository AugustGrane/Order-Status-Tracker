<script lang="ts">
    export let dialog: any;

    function handleClose() {
        if (dialog && dialog.open) {
            dialog.close();
        }
    }

    function handleDialogClose() {
        console.log("Dialog closed");
    }
</script>

<dialog
        bind:this={dialog}
        on:close={handleDialogClose}
>
    <div class="dialog-content">
        <div class="dialog-header">
            <button
                    type="button"
                    class="close-button"
                    on:click={handleClose}
                    aria-label="Close dialog"
            >×</button>
        </div>
        <div class="dialog-body">
            <slot/>
        </div>
    </div>
</dialog>

<style>
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
    }
    .dialog-header {
        display: flex;
        justify-content: flex-end;
        margin-bottom: 16px;
    }
    .close-button {
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
    .dialog-body {
        font-size: 16px;
        line-height: 1.5;
        color: #333;
    }
</style>