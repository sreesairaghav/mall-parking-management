/**
 * dashboard.js
 * Chart.js chart builders for the Mall Parking dashboard page.
 *
 * Shared utilities (clock, sidebar toggle, tooltips, progress bars)
 * are handled by common.js, which is loaded before this file.
 */
'use strict';

// ── Helpers ───────────────────────────────────────────────────────────────────

/**
 * Returns a rgba colour string with the given opacity.
 * @param {string} hex  – 6-char hex colour, e.g. "#2563eb"
 * @param {number} alpha
 */
function hexAlpha(hex, alpha) {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r},${g},${b},${alpha})`;
}

// ── Chart.js default overrides ────────────────────────────────────────────────
Chart.defaults.font.family = "'Inter', sans-serif";
Chart.defaults.font.size   = 12;
Chart.defaults.color       = '#94a3b8';

// ── Occupancy trend line chart ────────────────────────────────────────────────
function buildOccupancyChart(canvasId, occupancyData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const labels = Array.from({ length: 24 }, (_, i) => {
        const h = i % 12 || 12;
        return `${h}${i < 12 ? 'am' : 'pm'}`;
    });

    new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: 'Occupancy %',
                data: occupancyData,
                borderColor: '#3b82f6',
                backgroundColor: hexAlpha('#3b82f6', 0.08),
                fill: true,
                tension: 0.45,
                pointRadius: 0,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: '#3b82f6',
                borderWidth: 2.5,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#0d1b2a',
                    titleColor: '#e2e8f0',
                    bodyColor: '#94a3b8',
                    borderColor: '#1e3a5f',
                    borderWidth: 1,
                    padding: 10,
                    callbacks: {
                        label: ctx => ` ${ctx.parsed.y}% occupied`
                    }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { maxTicksLimit: 8 }
                },
                y: {
                    min: 0,
                    max: 100,
                    grid: { color: '#f1f5f9' },
                    ticks: {
                        callback: v => v + '%',
                        maxTicksLimit: 6
                    }
                }
            }
        }
    });
}

// ── Revenue line chart ────────────────────────────────────────────────────────
function buildRevenueChart(canvasId, revenueData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const labels = Array.from({ length: 24 }, (_, i) => {
        const h = i % 12 || 12;
        return `${h}${i < 12 ? 'am' : 'pm'}`;
    });

    new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: 'Revenue ₹',
                data: revenueData,
                borderColor: '#10b981',
                backgroundColor: hexAlpha('#10b981', 0.08),
                fill: true,
                tension: 0.45,
                pointRadius: 0,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: '#10b981',
                borderWidth: 2.5,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#0d1b2a',
                    titleColor: '#e2e8f0',
                    bodyColor: '#94a3b8',
                    borderColor: '#1e3a5f',
                    borderWidth: 1,
                    padding: 10,
                    callbacks: {
                        label: ctx => ` ₹${ctx.parsed.y.toLocaleString('en-IN')}`
                    }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { maxTicksLimit: 8 }
                },
                y: {
                    grid: { color: '#f1f5f9' },
                    ticks: {
                        callback: v => '₹' + (v >= 1000 ? (v / 1000).toFixed(0) + 'k' : v),
                        maxTicksLimit: 6
                    }
                }
            }
        }
    });
}

// ── Floor doughnut chart ──────────────────────────────────────────────────────
function buildFloorDoughnut(canvasId, floorData) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !floorData || floorData.length === 0) return;

    const labels   = floorData.map(f => f.name);
    const occupied = floorData.map(f => f.occupied);
    const colors   = ['#2563eb', '#10b981', '#f59e0b'];

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data: occupied,
                backgroundColor: colors,
                borderWidth: 0,
                hoverOffset: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        pointStyle: 'circle',
                        padding: 16,
                        font: { size: 12 }
                    }
                },
                tooltip: {
                    backgroundColor: '#0d1b2a',
                    titleColor: '#e2e8f0',
                    bodyColor: '#94a3b8',
                    borderColor: '#1e3a5f',
                    borderWidth: 1,
                    callbacks: {
                        label: ctx => ` ${ctx.label}: ${ctx.parsed} vehicles`
                    }
                }
            }
        }
    });
}

// ── Entry point ───────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const d = window.dashboardData || {};

    buildOccupancyChart('occupancyChart', d.hourlyOccupancy || []);
    buildRevenueChart('revenueChart',     d.hourlyRevenue   || []);
    buildFloorDoughnut('floorDoughnut',   d.floorData       || []);
});
