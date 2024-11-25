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
<select id="dropdown" class="dropdown" bind:value={selectedValue} on:change={displaycomponent}>
    <option value="default">Opret</option>
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

<style>
    .dropdown {
        padding: 0.75rem 2rem 0.75rem 1rem;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        background-color: white;
        font-size: 0.9rem;
        cursor: pointer;
        appearance: none;
        background-image: url(data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E);
        background-repeat: no-repeat;
        background-position: right 0.5rem center;
        background-size: 1.2em;
    }
</style>
