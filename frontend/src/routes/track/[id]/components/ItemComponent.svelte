<script lang="ts">
    import StepComponent from "./StepComponent.svelte";

    export let productType:string;
    export let id:number;
    export let quantity:number;
</script>


<div class="item"> <!-- Start of Item -->
    <div class="item-title">{productType} | #{id} | <i>Antal: {quantity}</i></div>
    <div class="timeline-wrapper">
        <StepComponent status="Modtaget" estimate="" done={true} firstItem={true} />
        <StepComponent status="Justeret" estimate="4 timer" current={true} />
        <StepComponent status="Printet" estimate="8 timer" />
        <StepComponent status="Tørret" estimate="1 dag" />
        <StepComponent status="Pakket" estimate="2 timer" />
        <StepComponent status="Afsendt" estimate="1 dag" />
    </div>
</div> <!-- End of Item -->

<style>
    /* Add a Google Font import for Roboto */
    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');

    :root {
        font-size: 16px;
        --font-primary: 'Roboto', Arial, sans-serif;
    }

    .item {
        display: flex;
        flex-direction: column;
        height: 130px;
        align-items: flex-start;
        justify-content: center;
        gap: 20px;
        padding: 5px 10px;
        position: relative;
        align-self: stretch;
        width: 100%;
        border-radius: 5px;
        overflow: hidden;
        border: 1px solid;
        border-color: #00000026;
    }

    .item-title {
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-size: 1.25rem;
        font-weight: 400;
        color: #000000;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .timeline-wrapper {
        display: flex;
        align-items: flex-start;
        justify-content: center;
        position: relative;
        align-self: stretch;
        width: 100%;
        flex: 0 0 auto;
    }
</style>