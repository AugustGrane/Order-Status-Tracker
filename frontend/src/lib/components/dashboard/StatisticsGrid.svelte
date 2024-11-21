<script lang="ts">
    export let totalOrders: number | undefined;
    export let activeOrders: number | undefined;
    export let completedOrders: number | undefined;
    export let priorityOrders: number | undefined;
    export let ordersCompletedToday: number | undefined;
    export let averageProcessingTime: number | undefined;
    export let productTypeStats: Array<{type: string, count: number}>;
</script>

<div class="statistics-grid">
    {#if priorityOrders !== undefined}
    <div class="stat-card">
        <div class="stat-icon">🔥️</div>
        <div class="stat-content">
            <h3>Prioritetsordre</h3>
            <p class="stat-value">{priorityOrders}</p>
        </div>
    </div>
    {/if}
    {#if activeOrders !== undefined}
    <div class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-content">
            <h3>Aktive Ordre</h3>
            <p class="stat-value">{activeOrders}</p>
        </div>
    </div>
    {/if}
    {#if averageProcessingTime !== undefined}
    <div class="stat-card">
        <div class="stat-icon">⏱️</div>
        <div class="stat-content">
            <h3>Gennemsnitlig Behandlingstid</h3>
            <p class="stat-value">{averageProcessingTime} dage</p>
        </div>
    </div>
    {/if}
    {#if ordersCompletedToday !== undefined}
    <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
            <h3>Afsluttet i Dag</h3>
            <p class="stat-value">{ordersCompletedToday}</p>
        </div>
    </div>
    {/if}
    {#if totalOrders !== undefined}
    <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
            <h3>Antal Ordre</h3>
            <p class="stat-value">{totalOrders}</p>
        </div>
    </div>
    {/if}
    {#if completedOrders !== undefined}
    <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
            <h3>Afsluttede Ordrer</h3>
            <p class="stat-value">{completedOrders}</p>
        </div>
    </div>
    {/if}
    {#if productTypeStats !== undefined}
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
    {/if}
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
        gap: 0.8rem;
    }

    .span-2 {
        grid-column: span 2;
    }

    .stat-icon {
        display: flex;
        font-size: 1.5rem;
        align-items: center;
        height: 50%;
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

    @media (max-width: 1300px) {
        .statistics-grid {
            grid-template-columns: repeat(auto-fit, minmax(100px, 9fr));
            gap: 0.5rem;
        }
        .stat-card {
            padding: 1rem;
        }
        .stat-icon {
            font-size: 1.3rem;
        }
        .stat-content h3 {
            font-size: 0.8rem;
        }
        .stat-value {
            font-size: 1.2rem;
        }
    }
</style>
