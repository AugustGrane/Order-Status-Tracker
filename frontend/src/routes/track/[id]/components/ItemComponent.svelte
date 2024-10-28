<script lang="ts">
    export let productType:string;
    export let id:number;
    export let quantity:number;
</script>


<div class="item"> <!-- Start of Item -->
    <div class="item-title">{productType} | #{id} | <i>Antal: {quantity}</i></div>
    <div class="timeline-wrapper">
        <div class="step">
            <div class="circle-wrapper">
                <div class="circle"></div>
                <div class="status-text">
                    <div class="status">Modtaget</div>
                    <div class="estimate"> </div>
                </div>
            </div>
        </div>
        <img class="back-line" src="https://c.animaapp.com/rBpIyes1/img/back-line.svg" />
        <div class="step">
            <div class="circle-wrapper">
                <div class="current-circle"></div>
                <div class="status-text">
                    <div class="current-status">Justeret</div>
                    <div class="current-estimate">(2 timer)</div>
                </div>
            </div>
        </div>
        <img class="back-line" src="https://c.animaapp.com/rBpIyes1/img/back-line-1.svg" />
        <div class="step">
            <div class="circle-wrapper">
                <div class="circle-2"></div>
                <div class="status-text">
                    <div class="status">Printet</div>
                    <div class="estimate">(4 timer)</div>
                </div>
            </div>
        </div>
        <img class="back-line" src="https://c.animaapp.com/rBpIyes1/img/back-line-2.svg" />
        <div class="step">
            <div class="circle-wrapper">
                <div class="circle-2"></div>
                <div class="status-text">
                    <div class="status">Tørret</div>
                    <div class="estimate">(1 dag)</div>
                </div>
            </div>
        </div>
        <img class="back-line" src="https://c.animaapp.com/rBpIyes1/img/back-line-3.svg" />
        <div class="step">
            <div class="circle-wrapper">
                <div class="circle-2"></div>
                <div class="status-text">
                    <div class="status">Pakket</div>
                    <div class="estimate">(2 timer)</div>
                </div>
            </div>
        </div>
        <img class="back-line" src="https://c.animaapp.com/rBpIyes1/img/back-line-4.svg" />
        <div class="step">
            <div class="circle-wrapper">
                <div class="circle-2"></div>
                <div class="status-text">
                    <div class="status">Afsendt</div>
                    <div class="estimate">(1 dag)</div>
                </div>
            </div>
        </div>
    </div>
</div> <!-- End of Item -->

<style>
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

    .step {
        display: flex;
        align-items: flex-start;
        justify-content: center;
        gap: 5px;
        padding: 0px 1px;
        position: relative;
        flex: 1;
        flex-grow: 1;
    }

    .circle-wrapper {
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        gap: 5px;
        position: relative;
        flex: 0 0 auto;
    }

    .circle {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #24a147;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .current-circle {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #1166ee;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .circle-2 {
        position: relative;
        width: 25px;
        height: 25px;
        background-color: #aaaaaa;
        border-radius: 15px;
        transform: rotate(180deg);
    }

    .status-text {
        display: inline-flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        position: relative;
        flex: 0 0 auto;
    }

    .current-status {
        color: #000000;
        position: relative;
        width: fit-content;
        margin-top: -1.00px;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .current-estimate {
        color: #000000;
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .status {
        color: #808080;
        position: relative;
        width: fit-content;
        margin-top: -1.00px;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .estimate {
        color: #808080;
        position: relative;
        width: fit-content;
        font-family: var(--font-primary);
        font-weight: 400;
        font-size: 0.9rem;
        text-align: center;
        letter-spacing: 0;
        line-height: normal;
        white-space: nowrap;
    }

    .back-line {
        position: relative;
        flex: 1;
        flex-grow: 1;
    }
</style>