<script lang="ts">
    import Dialog from './Dialog.svelte';
    let dialog: any;
</script>

<a
        href="#"
        class="help-link"
        on:click|preventDefault={() => dialog.showModal()}
>
    <span class="question-circle">?</span>
    How to find order number
</a>

<Dialog bind:dialog on:close={() => console.log('closed')}>
    <div class="dialog-header">
        <h2 class="dialog-title">Order Number Required</h2>
    </div>
    <div class="dialog-body">
        <p>You need to enter your order number. You should be able to find this order number in the email we sent you.</p>
        <p class="note">You can press ESC or click the × button to close</p>
    </div>
</Dialog>

<style>
    .help-link {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        color: #0066cc;
        text-decoration: none;
        cursor: pointer;
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
        font-size: 12px;
        font-weight: bold;
    }
</style>