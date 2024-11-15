<script lang="ts">
    export let totalOrders: number;
    export let activeOrders: number;
    export let completedOrders: number;
    export let priorityOrders: number;
    export let ordersCompletedToday: number;
    export let averageProcessingTime: number;
    export let productTypeStats: Array<{type: string, count: number}>;
</script>

<div class="statistics-grid">
    <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
            <h3>Total Orders</h3>
            <p class="stat-value">{totalOrders}</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon">⚡</div>
        <div class="stat-content">
            <h3>Active Orders</h3>
            <p class="stat-value">{activeOrders}</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
            <h3>Completed Orders</h3>
            <p class="stat-value">{completedOrders}</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon">🔥</div>
        <div class="stat-content">
            <h3>Priority Orders</h3>
            <p class="stat-value">{priorityOrders}</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
            <h3>Completed Today</h3>
            <p class="stat-value">{ordersCompletedToday}</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon">⏱️</div>
        <div class="stat-content">
            <h3>Avg. Processing Time</h3>
            <p class="stat-value">{averageProcessingTime} days</p>
        </div>
    </div>
    <div class="stat-card span-2">
        <div class="stat-icon">📈</div>
        <div class="stat-content">
            <h3>Top Product Types</h3>
            <div class="product-types">
                {#each productTypeStats as stat}
                    <div class="product-type">
                        <span>{stat.type}:</span>
                        <span>{stat.count}</span>
                    </div>
                {/each}
            </div>
        </div>
    </div>
</div>

<style>
    .statistics-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 1rem;
        margin-bottom: 2rem;
    }

    .stat-card {
        background: white;
        border-radius: 12px;
        padding: 1.5rem;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: flex-start;
        gap: 1rem;
    }

    .span-2 {
        grid-column: span 2;
    }

    .stat-icon {
        font-size: 1.5rem;
    }

    .stat-content {
        flex-grow: 1;
    }

    .stat-content h3 {
        margin: 0;
        font-size: 0.9rem;
        color: #666;
        font-weight: 500;
    }

    .stat-value {
        margin: 0.5rem 0 0;
        font-size: 1.5rem;
        font-weight: 600;
        color: #333;
    }

    .product-types {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 0.5rem;
        margin-top: 0.5rem;
    }

    .product-type {
        display: flex;
        justify-content: space-between;
        font-size: 0.9rem;
        color: #666;
    }
</style>
