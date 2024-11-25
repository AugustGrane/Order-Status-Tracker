<script lang="ts">
    import {onMount} from "svelte";
    import Dialog from "$lib/components/dialog/Dialog.svelte";
    import CreateStepComponent from "$lib/components/dashboard/create/CreateStepComponent.svelte";
    import CreateItemComponent from "$lib/components/dashboard/create/CreateItemComponent.svelte";
    import CreateOrderComponent from "$lib/components/dashboard/create/CreateOrderComponent.svelte";
    import CreateProductionTypeComponent from "$lib/components/dashboard/create/CreateProductionTypeComponent.svelte";

    export let orders;

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
    <option value="order">Opret Ordre</option>
    <option value="item">Opret Artikel</option>
    <option value="production-type">Opret Produktionstype</option>
    <option value="step">Opret Produktionstrin</option>
</select>

<Dialog title="Opret Ordre" bind:dialog={orderDialog}>
    <CreateOrderComponent orders={orders}/>
</Dialog>

<Dialog title="Opret Artikel" bind:dialog={itemDialog}>
    <CreateItemComponent orders={orders}/>
</Dialog>

<Dialog title="Opret Produktionstype" bind:dialog={productionTypeDialog}>
    <CreateProductionTypeComponent/>
</Dialog>

<Dialog title="Opret Produktionstrin" bind:dialog={productionStepDialog}>
    <CreateStepComponent/>
</Dialog>

