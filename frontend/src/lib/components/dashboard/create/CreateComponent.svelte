<script lang="ts">
    import {onMount} from "svelte";
    import Dialog from "$lib/components/dialog/Dialog.svelte";
    onMount(() => {
        const dropdown = document.getElementById("dropdown") as HTMLSelectElement;

    });

    let selectedValue = "default";
    let orderDialog: any;
    let itemDialog: any;
    let productionTypeDialog: any;
    let productionStepDialog: any;

    function displaycomponent()
    {
        if (selectedValue != "default") {
            switch (selectedValue) {
                case "order":
                    console.log("order")
                    orderDialog.showModal()
                    break;
                case "item":
                    console.log("item")
                    itemDialog.showModal()
                    break;
                case "production-type":
                    productionTypeDialog.showModal()
                    break;
                case "step":
                    productionStepDialog.showModal()
                    break;
                default:
                    break;
            }
        }
    }



</script>
<label for="dropdown"> Opret</label>
<select id="dropdown" bind:value={selectedValue} on:change={displaycomponent}>
    <option value="default">Opret</option>
    <option value="order">Opret ordre</option>
    <option value="item">Opret artikel</option>
    <option value="production-type">Opret produkttype</option>
    <option value="step">Opret produktionstrin</option>
</select>

<Dialog title="Opret Ordre" bind:dialog={orderDialog}>
    <div class="dialog-body">
        <p>lav ordre</p>
    </div>
</Dialog>

<Dialog title="Opret Artikel" bind:dialog={itemDialog}>
    <div class="dialog-body">
        <p>lav item</p>
    </div>
</Dialog>

<Dialog title="Opret produktionstype" bind:dialog={productionTypeDialog}>
    <div class="dialog-body">
        <p>lav item</p>
    </div>
</Dialog>

<Dialog title="Opret Produktionstrin" bind:dialog={productionStepDialog}>
    <div class="dialog-body">
        <p>lav item</p>
    </div>
</Dialog>

