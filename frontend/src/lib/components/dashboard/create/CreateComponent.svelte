<script lang="ts">
    import {onMount} from "svelte";
    import Dialog from "$lib/components/dialog/Dialog.svelte";
    import CreateStepComponent from "$lib/components/dashboard/create/CreateStepComponent.svelte";
    import CreateItemComponent from "$lib/components/dashboard/create/CreateItemComponent.svelte";
    import CreateOrderComponent from "$lib/components/dashboard/create/CreateOrderComponent.svelte";
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

        selectedValue = "default";
    }

</script>
<label for="dropdown"> Opret</label><br>
<select id="dropdown" bind:value={selectedValue} on:change={displaycomponent}>
    <option value="default">Vælg</option>
    <option value="order">Opret ordre</option>
    <option value="item">Opret artikel</option>
    <option value="production-type">Opret produkttype</option>
    <option value="step">Opret produktionstrin</option>
</select>

<Dialog title="Opret Ordre" bind:dialog={orderDialog}>
    <CreateOrderComponent/>
</Dialog>

<Dialog title="Opret Artikel" bind:dialog={itemDialog}>
    <CreateItemComponent/>
</Dialog>

<Dialog title="Opret Produktionstype" bind:dialog={productionTypeDialog}>
</Dialog>

<Dialog title="Opret Produktionstrin" bind:dialog={productionStepDialog}>
    <CreateStepComponent/>
</Dialog>

