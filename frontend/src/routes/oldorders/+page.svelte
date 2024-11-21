<script lang="ts">
    import type { PageData } from './$types';
    export let data: PageData;
    const orders = data.orders; // Filtered orders

    console.log("filtered orders: ", orders);

</script>

<main>
    <div class="background">
        <div class="navbar">
            <div class="logo"></div>
            <div class="title">Gamle Ordrer</div>
        </div>
        <div class="container">
            <div class="search-filter">
                <div class="left">
                    <div class="sub-title">Gamle Ordrer</div>
                    <div class="active-orders">Se de færdige ordrer</div>
                    <div class="search"><input class="search-field" placeholder="Søg efter navn, ordrenummer eller artikler"></div>
                </div>
                <div class="right">
                    <select class="dropdown-filter">
                        <option>Nyeste</option>
                        <option>Ældste</option>
                        <option>Efter Ordrenummer</option>
                        <option>Efter Navn</option>
                        
                    </select>
                </div>
            </div>
            <div class="order-overview">
                <div class="name-bar">
                    <div class="order-number">Ordrenummer</div>
                    <div class="date">Dato</div>
                    <div class="customer">Kundens navn</div>
                    <div class="items-and-amount">Artikler, mængde og status</div>
                </div>
                <div class="order">
                    {#each orders as order}
                        <div class="order-container">
                            <div class="actual-order-number">{order.orderId}</div>
                            <div class="actual-date">{new Date(order.orderCreated).toLocaleDateString()}</div>
                            <div class="actual-customer">{order.customerName}</div>
                            <div class="item-status-container">
                                {#each order.items as item}
                                    <div class="item-status-instance">
                                        <div class="actual-item">{item.item.name} &nbsp-&nbsp  {item.itemAmount}</div>
                                        <div class="actual-status">
                                            <select class="dropdown-status">
                                                {#each item.differentSteps as step}
                                                    {#if step.name == item.differentSteps[item.currentStepIndex].name}
                                                        <option selected>{item.differentSteps[item.currentStepIndex].name}&nbsp;</option>
                                                    {:else}
                                                        <option>{step.name}&nbsp;</option>
                                                    {/if}
                                                {/each}
                                            </select>
                                        </div>
                                    </div>
                                {/each}
                            </div>
                        </div>
                    {/each}
                </div>
            </div>
        </div>
    </div>
</main>

<style>

    /*{#if orders} <!-- Now we can check just orders -->
        {#each orders as order}
            <p>Order ID: {order.orderId}</p>
            <p>Date created: {new Date(order.orderCreated).toLocaleDateString()}</p>
            <p>Customer name: {order.customerName}</p>
            <h3>Products:
                {#each order.items as item}
                    Name: {item.item.name},
                    Amount: {item.itemAmount},
                    Status: {item.differentSteps[item.currentStepIndex].name}&nbsp;
                {/each}
            </h3>
            <p>Notes: {order.Notes}</p>
            <br>
        {/each}
    {:else}
        <p>Loading...</p>
    {/if}*/

    @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
    html {
        font-family: Roboto, Arial, sans-serif;
    }

    .background {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1.5rem; /* Use relative units */
        position: relative;
        background-color: #ffffff;
        padding: 1.5rem;
    }

    .navbar {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 1rem;
        width: 100%;
        background-color: #ffffff;
        padding: 0.5rem 1rem; /* Add padding */
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .logo {
        width: 10%;
        max-width: 100px; /* Limit max size */
        aspect-ratio: 2 / 1; /* Ensure it scales proportionally */
        background-image: url('/uploads/gtryk_logo.png');
        background-size: cover;
    }

    .title {
        font-size: 2rem; /* Scales with screen size */
        font-weight: 400;
        color: #000;
    }

    .container {
        display: flex;
        flex-direction: column;
        width: 95%; /* Use relative width */
        max-width: 1200px; /* Optional max width */
        background-color: rgba(32, 52, 152, 0.61);
        border-radius: 0.5rem;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        padding: 1.5rem; /* Add padding */
    }

    .search-filter {
        display: flex;
        flex-wrap: wrap; /* Wrap elements on small screens */
        gap: 10rem;
        justify-content: space-between;
        align-items: flex-end;
        width: 100%;
        margin-bottom: 1rem;
    }

    .left{
        flex: 1; /* Distribute space */
        min-width: 200px; /* Prevent elements from shrinking too small */
    }

    .right {
        display: flex; /* Enable flexbox */
        flex: 1; /* Distribute space within the parent container */
        min-width: 200px; /* Prevent elements from becoming too small */
        align-items: flex-end; /* Align items vertically to the end of the container */
        justify-content: flex-end; /* Align items horizontally to the right */
    }

    .sub-title{
        font-size: 1.6rem; /* Scalable size */
        margin-bottom: 0.5rem;
        font-weight: bold;
    }

    .active-orders {
        font-size: 1.2rem; /* Scalable size */
        margin-bottom: 0.5rem;
    }

    .search-field {
        width: 90%; /* Make inputs scale */
        padding: 5px;
        font-size: 1rem;
        border-radius: 0.5rem;
        border: 1px solid #ccc;
    }

    .dropdown-filter {
        width: 50%; /* Make inputs scale */
        padding: 5px;
        font-size: 1rem;
        border-radius: 0.5rem;
        border: 1px solid #ccc;
    }

    .order-overview {
        display: flex;
        flex-direction: column;
        width: 100%;
        gap: 0.3rem;
    }

    .name-bar {
        display: flex; /* Enable flexbox */
        justify-content: space-between; /* Ensure spacing between items */
        align-items: center; /* Align items vertically in the center */
        padding: 20px 25px;
        background-color: #c3c3c3;
        border-radius: 15px;
        font-size: 1.1rem;
        font-weight: bold;
    }

    .order-container {
        display: flex; /* Enable flexbox */
        justify-content: space-between; /* Ensure spacing between items */
        align-items: center; /* Align items vertically in the center */
        padding: 20px 25px;
        background-color: #c3c3c3;
        border-radius: 15px;
        margin-bottom: 5px;
    }

    .order-number,
    .date,
    .customer {
        flex: 1; /* Each element takes an equal share of 3/5 */
        max-width: 10%; /* Distribute the 3/5 proportion */
        min-width: 10%;
    }

    .items-and-amount{
        flex: 2; /* Takes 2/5 of the width */
        max-width: calc(2 / 5 * 100%); /* Prevents overflow */
        display: flex;
        flex-direction: column;
        gap: 10px; /* Adjust spacing for children if needed */
        padding-right: 20px;
    }

    .actual-order-number,
    .actual-date,
    .actual-customer {
        flex: 1; /* Each element takes an equal share of 3/5 */
        max-width: 10%; /* Distribute the 3/5 proportion */
        min-width: 10%;
    }

    .order-container {
        background-color: #c3c3c3;
    }
    .order-container > *:last-child {
        margin-right: 0; /* Remove spacing for the last element */
    }

    .item-status-container {
        flex: 2; /* Takes 2/5 of the width */
        max-width: calc(2 / 5 * 100%); /* Prevents overflow */
        display: flex;
        flex-direction: column;
        gap: 10px; /* Adjust spacing for children if needed */
        padding-right: 20px;

    }

    .item-status-instance {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 1rem;
        padding: 0.5rem 1rem;
        background-color: #e3e3e3;
        border-radius: 0.5rem;
        width: 100%; /* Ensure it takes the full parent width */
        max-width: 600px; /* Adjust this value to make it wider */
    }

    .dropdown-status {
        width: 100%; /* Scales with container */
        padding: 0.5rem;
        font-size: 1rem;
    }

</style>