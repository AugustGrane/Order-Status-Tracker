<script lang="ts">
    export let searchQuery: string;
    export let sortOption: string;
    export let statusFilter: string;
</script>

<div class="filters">
    <div class="search-bar">
        <input
            type="text"
            placeholder="Search by order number, customer name, or product..."
            bind:value={searchQuery}
        >
    </div>
    <div class="filter-options">
        <select bind:value={sortOption}>
            <option value="newest">Newest First</option>
            <option value="oldest">Oldest First</option>
            <option value="process">Process Size</option>
        </select>
        <select bind:value={statusFilter}>
            <option value="active">Active Orders</option>
            <option value="completed">Completed Orders</option>
            <option value="all">All Orders</option>
        </select>
    </div>
</div>

<style>
    .filters {
        display: flex;
        gap: 1rem;
        margin-bottom: 1.5rem;
        flex-wrap: wrap;
    }

    .search-bar {
        flex: 1;
        min-width: 300px;
    }

    .search-bar input {
        width: 100%;
        padding: 0.75rem 1rem;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        font-size: 0.9rem;
    }

    .filter-options {
        display: flex;
        gap: 1rem;
    }

    select {
        padding: 0.75rem 2rem 0.75rem 1rem;
        border: 1px solid #e2e8f0;
        border-radius: 8px;
        background-color: white;
        font-size: 0.9rem;
        cursor: pointer;
        appearance: none;
        background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
        background-repeat: no-repeat;
        background-position: right 0.5rem center;
        background-size: 1.2em;
    }

    select:focus, .search-bar input:focus {
        outline: none;
        border-color: #3b82f6;
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }
</style>
