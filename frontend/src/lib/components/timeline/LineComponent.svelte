<script lang="ts">
    export let done: boolean = false;
    export let current: boolean = false;
</script>

<div class="line" class:done={done} class:current={current}></div>

<style>
    .line {
        position: relative;
        flex: 1;
        height: 3px;
        background-color: #dddddd;
        margin-top: 24px;
        transition: all 0.3s ease;
    }

    .line.done {
        background-color: #24a147;
    }

    .line.current {
        background-color: #1166ee;
    }
    @media (max-width: 1000px){
        .line{
            opacity: 0%;
        }
    }
</style>
