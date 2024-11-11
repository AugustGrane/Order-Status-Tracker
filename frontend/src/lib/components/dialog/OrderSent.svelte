<script lang="ts">
    import Dialog from './Dialog.svelte';
    import {onMount} from "svelte";
    let dialog: any;

    onMount(() => {
        dialog.showModal();
    });
</script>

<Dialog title="Ordre afsendt!" bind:dialog >
    <div class="dialog-body">
        <div class="icon-wrapper">
            <div class="icon"
                 style="background: url('/Shipping%20icon.png') no-repeat center;">
            </div>
        </div>
        <p>Din ordre er nu på vej til din adresse!</p>
        <p>Spor din pakke her: [link]</p>
    </div>
</Dialog>


<style>
    .dialog-body {
        text-align: center;
    }

    .icon-wrapper {
        display: flex;
        justify-content: center;
        margin-bottom: 1rem;
    }

    .icon {
        width: 100px;
        height: 100px;
        background-size: contain !important;
        transition: all 0.3s ease;
    }
</style>