/**
 * dashboard-live.js
 * Refreshes the dashboard from the application's PostgreSQL-backed REST APIs.
 * The page is initially server-rendered by Thymeleaf; this script keeps the
 * summary, floor overview and recent activity fresh without a full page reload.
 */
'use strict';

(() => {
    const REFRESH_INTERVAL_MS = 30000;

    const byId = (id) => document.getElementById(id);
    const setText = (id, value) => {
        const node = byId(id);
        if (node && value !== null && value !== undefined) node.textContent = value;
    };

    const money = (value) => Number(value || 0).toLocaleString('en-IN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    async function getJson(url) {
        const response = await fetch(url, {
            headers: { 'Accept': 'application/json' },
            cache: 'no-store'
        });
        if (!response.ok) {
            throw new Error(`Request ${url} failed with HTTP ${response.status}`);
        }
        return response.json();
    }

    function updateSummary(stats) {
        setText('totalSlotsValue', stats.totalSlots);
        setText('occupiedSlotsValue', stats.occupiedSlots);
        setText('availableSlotsValue', stats.availableSlots);
        setText('occupancyRateValue', Number(stats.occupancyPercentage || 0).toFixed(1));
        setText('todayRevenueValue', money(stats.todayRevenue));
        setText('carsTodayValue', stats.carsParkedToday);
        setText('averageRevenueValue', stats.carsParkedToday > 0
            ? Math.round(Number(stats.todayRevenue || 0) / stats.carsParkedToday).toLocaleString('en-IN')
            : '0');
        setText('occupancyMiniValue', Number(stats.occupancyPercentage || 0).toFixed(1));
    }

    function updateFloors(floors) {
        const rows = document.querySelectorAll('.dashboard-floor-row');
        const byCode = new Map(floors.map(floor => [String(floor.floorCode).toUpperCase(), floor]));

        rows.forEach(row => {
            const floor = byCode.get(String(row.dataset.floorCode || '').toUpperCase());
            if (!floor) return;

            const total = row.querySelector('.floor-total');
            const occupied = row.querySelector('.floor-occupied');
            const available = row.querySelector('.floor-available');
            const percentage = row.querySelector('.floor-occupancy');
            const bar = row.querySelector('.occ-bar-fill');
            const badge = row.querySelector('.floor-status');

            if (total) total.textContent = floor.totalSlots;
            if (occupied) occupied.textContent = floor.occupiedSlots;
            if (available) available.textContent = floor.availableSlots;

            const pct = Number(floor.occupancyPercentage || 0);
            if (percentage) percentage.textContent = `${pct.toFixed(1)}%`;
            if (bar) {
                bar.dataset.pct = pct;
                bar.style.width = `${Math.max(0, Math.min(100, pct))}%`;
            }
            if (badge) {
                badge.textContent = floor.statusBadge;
                badge.className = `status-badge floor-status ${String(floor.statusBadge || '').toLowerCase()}`;
            }
        });

        // Update the existing Chart.js doughnut, if it was successfully created.
        const chart = window.Chart && Chart.getChart('floorDoughnut');
        if (chart) {
            chart.data.labels = floors.map(f => f.floorName);
            chart.data.datasets[0].data = floors.map(f => f.occupiedSlots);
            chart.update();
        }
    }

    function updateActivity(events) {
        const feed = byId('activityFeed');
        if (!feed) return;

        feed.replaceChildren();
        if (!events || events.length === 0) {
            const empty = document.createElement('li');
            empty.className = 'activity-item';
            empty.textContent = 'No vehicle activity recorded today.';
            feed.appendChild(empty);
            return;
        }

        events.forEach(event => {
            const isEntry = String(event.eventType).toUpperCase() === 'ENTRY';
            const item = document.createElement('li');
            item.className = 'activity-item';

            const iconWrap = document.createElement('div');
            iconWrap.className = `activity-event-icon ${isEntry ? 'entry' : 'exit'}`;
            const icon = document.createElement('i');
            icon.className = isEntry ? 'bi bi-box-arrow-in-down-right' : 'bi bi-box-arrow-up-right';
            iconWrap.appendChild(icon);

            const details = document.createElement('div');
            details.className = 'activity-details';
            const vehicle = document.createElement('div');
            vehicle.className = 'activity-vehicle';
            vehicle.textContent = event.vehicleNumber || 'Unknown vehicle';
            const meta = document.createElement('div');
            meta.className = 'activity-meta';
            const driver = document.createElement('span');
            driver.textContent = event.driverName || 'Unknown driver';
            const location = document.createElement('span');
            location.textContent = `${event.floor || '-'} › ${event.slot || '-'}`;
            meta.append(driver, document.createTextNode(' • '), location);
            details.append(vehicle, meta);

            const right = document.createElement('div');
            right.className = 'activity-right';
            const time = document.createElement('div');
            time.className = 'activity-time';
            time.textContent = event.time || '';
            const badge = document.createElement('span');
            badge.className = `event-badge ${isEntry ? 'entry' : 'exit'}`;
            badge.textContent = isEntry ? 'Entry' : 'Exit';
            right.append(time, badge);

            item.append(iconWrap, details, right);
            feed.appendChild(item);
        });
    }

    async function refreshDashboard() {
        try {
            const [summary, floors, activity] = await Promise.all([
                getJson('/api/dashboard/summary'),
                getJson('/api/dashboard/floors'),
                getJson('/api/dashboard/activity')
            ]);
            updateSummary(summary);
            updateFloors(floors);
            updateActivity(activity);
            document.documentElement.dataset.dashboardApiStatus = 'connected';
        } catch (error) {
            console.error('Dashboard refresh failed:', error);
            document.documentElement.dataset.dashboardApiStatus = 'error';
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        refreshDashboard();
        window.setInterval(refreshDashboard, REFRESH_INTERVAL_MS);
    });
})();
