/**
 * common.js
 * Shared utilities loaded on EVERY page of the Mall Parking admin dashboard.
 *
 * Responsibilities:
 *  · Mobile sidebar toggle + overlay
 *  · Live clock in the topbar
 *  · Bootstrap tooltip init
 *  · Occupancy progress bar animation (for pages that use .occ-bar-fill)
 *
 * dashboard.js is loaded ONLY on the dashboard page and handles Chart.js.
 */
'use strict';

// ── Live clock ────────────────────────────────────────────────────────────────
function startLiveClock(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    function tick() {
        const now = new Date();
        el.textContent = now.toLocaleTimeString('en-IN', {
            hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: true
        });
    }
    tick();
    setInterval(tick, 1000);
}

// ── Sidebar mobile toggle ─────────────────────────────────────────────────────
function setupSidebarToggle() {
    const btn     = document.getElementById('sidebarToggleBtn');
    const sidebar = document.getElementById('mainSidebar');
    const overlay = document.getElementById('sidebarOverlay');
    if (!btn || !sidebar) return;
    function open()  {
        sidebar.classList.add('open');
        if (overlay) { overlay.style.display = 'block'; overlay.classList.add('show'); }
    }
    function close() {
        sidebar.classList.remove('open');
        if (overlay) { overlay.style.display = 'none'; overlay.classList.remove('show'); }
    }
    btn.addEventListener('click', () => sidebar.classList.contains('open') ? close() : open());
    if (overlay) overlay.addEventListener('click', close);
}

// ── Bootstrap tooltip init ────────────────────────────────────────────────────
function initTooltips() {
    if (typeof bootstrap === 'undefined') return;
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
        new bootstrap.Tooltip(el, { trigger: 'hover' });
    });
}

// ── Animate progress bars (pages with .occ-bar-fill[data-pct]) ───────────────
function animateProgressBars() {
    document.querySelectorAll('.occ-bar-fill[data-pct]').forEach(bar => {
        const pct = parseFloat(bar.dataset.pct) || 0;
        const statusClass = pct >= 95 ? 'full'
                          : pct >= 70 ? 'high'
                          : pct >= 40 ? 'moderate'
                          : 'low';
        bar.classList.add(statusClass);
        requestAnimationFrame(() => {
            setTimeout(() => { bar.style.width = pct + '%'; }, 80);
        });
    });
}

// ── Entry point ───────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    startLiveClock('liveClock');
    setupSidebarToggle();
    initTooltips();
    animateProgressBars();
});
