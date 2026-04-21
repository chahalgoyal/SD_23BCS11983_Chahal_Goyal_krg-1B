/**
 * Restaurant Table Booking System — Frontend Application
 * 
 * Architecture: Single Page Application (SPA)
 * - Section-based navigation (no page reloads)
 * - RESTful API communication via Fetch API
 * - Dynamic DOM manipulation
 * - Toast notification system
 * 
 * API Base URL: /api
 */

const API_BASE = '/api';

// ===== Navigation =====

function navigateTo(sectionId) {
    // Hide all sections
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    
    // Show target section
    const target = document.getElementById('section-' + sectionId);
    if (target) target.classList.add('active');
    
    // Update nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.toggle('active', link.dataset.section === sectionId);
    });

    // Load data for the section
    if (sectionId === 'tables') loadTables();
    if (sectionId === 'admin') loadAdminData();

    // Close mobile menu
    document.querySelector('.nav-links')?.classList.remove('open');
}

// Navigation event listeners
document.addEventListener('DOMContentLoaded', () => {
    // Nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            navigateTo(link.dataset.section);
        });
    });

    // Logo → home
    document.getElementById('nav-logo')?.addEventListener('click', (e) => {
        e.preventDefault();
        navigateTo('home');
    });

    // Mobile toggle
    document.getElementById('nav-toggle')?.addEventListener('click', () => {
        document.querySelector('.nav-links')?.classList.toggle('open');
    });

    // Set today's date as minimum for date inputs
    const today = new Date().toISOString().split('T')[0];
    const searchDate = document.getElementById('search-date');
    if (searchDate) {
        searchDate.min = today;
        searchDate.value = today;
    }

    // Default time
    const searchTime = document.getElementById('search-time');
    if (searchTime) searchTime.value = '19:00';

    // Search form
    document.getElementById('search-form')?.addEventListener('submit', handleSearchTables);

    // Booking form
    document.getElementById('booking-form')?.addEventListener('submit', handleBooking);

    // Lookup form
    document.getElementById('lookup-form')?.addEventListener('submit', handleLookup);
});


// ===== Tables =====

async function loadTables() {
    const grid = document.getElementById('tables-grid');
    if (!grid) return;
    
    grid.innerHTML = '<div class="empty-state"><div class="spinner"></div><p>Loading tables...</p></div>';

    try {
        const response = await fetch(`${API_BASE}/tables`);
        const tables = await response.json();
        renderTables(tables, 'all');
    } catch (error) {
        grid.innerHTML = '<div class="empty-state"><p>Failed to load tables</p></div>';
        showToast('Failed to load tables', 'error');
    }
}

let allTables = [];

function renderTables(tables, filter) {
    allTables = tables;
    const grid = document.getElementById('tables-grid');
    
    const filtered = filter === 'all' 
        ? tables 
        : tables.filter(t => t.location === filter);

    if (filtered.length === 0) {
        grid.innerHTML = '<div class="empty-state"><div class="empty-icon">🪑</div><h3>No tables found</h3></div>';
        return;
    }

    grid.innerHTML = filtered.map(table => `
        <div class="table-card" data-location="${table.location}">
            <div class="table-card-header">
                <div class="table-number">Table <span>#${table.tableNumber}</span></div>
                <span class="table-status ${table.active ? 'status-active' : 'status-inactive'}">
                    ${table.active ? '● Active' : '● Inactive'}
                </span>
            </div>
            <div class="table-details">
                <div class="table-detail">
                    <div class="table-detail-icon">👥</div>
                    <span>Seats up to <strong>${table.capacity}</strong> guests</span>
                </div>
                <div class="table-detail">
                    <div class="table-detail-icon">${getLocationIcon(table.location)}</div>
                    <span>${table.location}</span>
                </div>
            </div>
            <div class="table-card-actions">
                <button class="btn btn-primary btn-sm btn-full" onclick="quickBook(${table.id})">
                    Book This Table
                </button>
            </div>
        </div>
    `).join('');
}

function getLocationIcon(location) {
    const icons = {
        'Main Hall': '🏛️',
        'Window': '🪟',
        'Patio': '🌿',
        'Private Room': '🚪'
    };
    return icons[location] || '📍';
}

// Filter buttons
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('filter-btn')) {
        document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
        e.target.classList.add('active');
        renderTables(allTables, e.target.dataset.filter);
    }
});

function quickBook(tableId) {
    navigateTo('book');
}


// ===== Search Available Tables =====

async function handleSearchTables(e) {
    e.preventDefault();

    const date = document.getElementById('search-date').value;
    const time = document.getElementById('search-time').value;
    const partySize = document.getElementById('search-party-size').value;

    if (!date || !time || !partySize) {
        showToast('Please fill in all search fields', 'error');
        return;
    }

    const btn = document.getElementById('search-tables-btn');
    btn.innerHTML = '<span class="spinner"></span> Searching...';
    btn.disabled = true;

    try {
        const response = await fetch(
            `${API_BASE}/tables/available?date=${date}&time=${time}:00&partySize=${partySize}`
        );
        const tables = await response.json();

        const resultDiv = document.getElementById('available-tables-result');
        const listDiv = document.getElementById('available-tables-list');
        resultDiv.style.display = 'block';

        if (tables.length === 0) {
            listDiv.innerHTML = `
                <div class="empty-state" style="padding: 1.5rem;">
                    <div class="empty-icon">😔</div>
                    <h3>No tables available</h3>
                    <p>Try a different date, time, or party size</p>
                </div>`;
            return;
        }

        listDiv.innerHTML = tables.map(table => `
            <div class="available-table-item" onclick="selectTable(${table.id}, ${table.tableNumber}, '${table.location}', ${table.capacity})" data-table-id="${table.id}">
                <div class="available-table-info">
                    <div class="available-table-icon">${getLocationIcon(table.location)}</div>
                    <div class="available-table-details">
                        <h5>Table #${table.tableNumber}</h5>
                        <span>${table.location}</span>
                    </div>
                </div>
                <div class="available-table-capacity">${table.capacity} seats</div>
            </div>
        `).join('');

        showToast(`${tables.length} table(s) available!`, 'success');
    } catch (error) {
        showToast('Search failed. Please try again.', 'error');
    } finally {
        btn.innerHTML = '<span>Search Available Tables</span><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>';
        btn.disabled = false;
    }
}


// ===== Select Table =====

function selectTable(id, number, location, capacity) {
    // Update hidden fields
    document.getElementById('selected-table-id').value = id;
    document.getElementById('selected-date').value = document.getElementById('search-date').value;
    document.getElementById('selected-time').value = document.getElementById('search-time').value;
    document.getElementById('selected-party-size').value = document.getElementById('search-party-size').value;

    // Show selection info
    const infoDiv = document.getElementById('selected-table-info');
    infoDiv.style.display = 'block';
    document.getElementById('selected-table-display').textContent = 
        `Table #${number} — ${location} (${capacity} seats)`;

    // Highlight selected
    document.querySelectorAll('.available-table-item').forEach(item => {
        item.classList.toggle('selected', parseInt(item.dataset.tableId) === id);
    });

    // Enable booking button
    document.getElementById('confirm-booking-btn').disabled = false;

    showToast(`Table #${number} selected`, 'info');
}


// ===== Make Reservation =====

async function handleBooking(e) {
    e.preventDefault();

    const tableId = document.getElementById('selected-table-id').value;
    if (!tableId) {
        showToast('Please select a table first (Step 1)', 'error');
        return;
    }

    const payload = {
        customerName: document.getElementById('customer-name').value,
        customerEmail: document.getElementById('customer-email').value,
        customerPhone: document.getElementById('customer-phone').value,
        tableId: parseInt(tableId),
        reservationDate: document.getElementById('selected-date').value,
        reservationTime: document.getElementById('selected-time').value,
        partySize: parseInt(document.getElementById('selected-party-size').value),
        specialRequests: document.getElementById('special-requests').value
    };

    const btn = document.getElementById('confirm-booking-btn');
    btn.innerHTML = '<span class="spinner"></span> Booking...';
    btn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/reservations`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Booking failed');
        }

        // Show success modal
        showSuccessModal(data, payload);

        // Reset forms
        document.getElementById('booking-form').reset();
        document.getElementById('search-form').reset();
        document.getElementById('selected-table-info').style.display = 'none';
        document.getElementById('available-tables-result').style.display = 'none';
        document.getElementById('confirm-booking-btn').disabled = true;

        // Reset date
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('search-date').value = today;
        document.getElementById('search-time').value = '19:00';

    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        btn.innerHTML = '<span>Confirm Reservation</span><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6 9 17l-5-5"/></svg>';
        btn.disabled = false;
    }
}


// ===== Lookup Reservations =====

async function handleLookup(e) {
    e.preventDefault();

    const email = document.getElementById('lookup-email').value;
    if (!email) return;

    const listDiv = document.getElementById('my-reservations-list');
    listDiv.innerHTML = '<div class="empty-state"><div class="spinner"></div><p>Searching...</p></div>';

    try {
        const response = await fetch(`${API_BASE}/reservations/search?email=${encodeURIComponent(email)}`);
        const reservations = await response.json();

        if (reservations.length === 0) {
            listDiv.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📋</div>
                    <h3>No reservations found</h3>
                    <p>No bookings found for ${email}</p>
                </div>`;
            return;
        }

        renderReservationCards(reservations, listDiv);
    } catch (error) {
        showToast('Lookup failed', 'error');
        listDiv.innerHTML = '<div class="empty-state"><p>Search failed. Try again.</p></div>';
    }
}

function renderReservationCards(reservations, container) {
    container.innerHTML = reservations.map(r => `
        <div class="reservation-card" style="animation-delay: ${reservations.indexOf(r) * 0.05}s">
            <div class="reservation-info">
                <h4>Table #${r.table.tableNumber} — ${r.table.location}</h4>
                <div class="reservation-meta">
                    <span class="meta-item">📅 ${formatDate(r.reservationDate)}</span>
                    <span class="meta-item">🕐 ${formatTime(r.reservationTime)}</span>
                    <span class="meta-item">👥 ${r.partySize} guest${r.partySize > 1 ? 's' : ''}</span>
                </div>
                ${r.specialRequests ? `<div class="reservation-meta"><span class="meta-item">💬 ${r.specialRequests}</span></div>` : ''}
            </div>
            <div class="reservation-actions">
                <span class="status-badge status-${r.status}">${r.status}</span>
                ${r.status === 'CONFIRMED' || r.status === 'PENDING' 
                    ? `<button class="btn btn-danger btn-sm" onclick="cancelReservation(${r.id})">Cancel</button>` 
                    : ''}
            </div>
        </div>
    `).join('');
}


// ===== Cancel Reservation =====

async function cancelReservation(id) {
    if (!confirm('Are you sure you want to cancel this reservation?')) return;

    try {
        const response = await fetch(`${API_BASE}/reservations/${id}/cancel`, { method: 'PUT' });
        const data = await response.json();

        if (!response.ok) throw new Error(data.error || 'Cancel failed');

        showToast('Reservation cancelled successfully', 'success');

        // Refresh the current view
        const lookupEmail = document.getElementById('lookup-email')?.value;
        if (lookupEmail) {
            document.getElementById('lookup-form')?.dispatchEvent(new Event('submit'));
        }
        loadAdminData();
    } catch (error) {
        showToast(error.message, 'error');
    }
}


// ===== Admin Panel =====

async function loadAdminData() {
    try {
        const [reservationsRes, tablesRes] = await Promise.all([
            fetch(`${API_BASE}/reservations`),
            fetch(`${API_BASE}/tables/all`)
        ]);

        const reservations = await reservationsRes.json();
        const tables = await tablesRes.json();

        // Stats
        const confirmed = reservations.filter(r => r.status === 'CONFIRMED').length;
        const cancelled = reservations.filter(r => r.status === 'CANCELLED').length;
        const activeTables = tables.filter(t => t.active).length;

        document.getElementById('admin-total').textContent = reservations.length;
        document.getElementById('admin-confirmed').textContent = confirmed;
        document.getElementById('admin-cancelled').textContent = cancelled;
        document.getElementById('admin-tables-count').textContent = activeTables;

        // Table
        const tbody = document.getElementById('admin-reservations-body');
        if (reservations.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; padding:2rem; color:var(--text-muted);">No reservations yet</td></tr>';
            return;
        }

        tbody.innerHTML = reservations.map(r => `
            <tr>
                <td>#${r.id}</td>
                <td>
                    <strong>${r.customer.name}</strong><br>
                    <span style="font-size:0.78rem;color:var(--text-muted)">${r.customer.email}</span>
                </td>
                <td>Table #${r.table.tableNumber}<br><span style="font-size:0.78rem;color:var(--text-muted)">${r.table.location}</span></td>
                <td>${formatDate(r.reservationDate)}</td>
                <td>${formatTime(r.reservationTime)}</td>
                <td>${r.partySize}</td>
                <td><span class="status-badge status-${r.status}">${r.status}</span></td>
                <td>
                    ${r.status === 'CONFIRMED' || r.status === 'PENDING'
                        ? `<button class="btn btn-danger btn-sm" onclick="cancelReservation(${r.id})">Cancel</button>`
                        : '—'}
                </td>
            </tr>
        `).join('');

    } catch (error) {
        showToast('Failed to load admin data', 'error');
    }
}


// ===== Success Modal =====

function showSuccessModal(reservation, payload) {
    const details = document.getElementById('modal-details');
    details.innerHTML = `
        <strong>Reservation ID:</strong> #${reservation.id}<br>
        <strong>Name:</strong> ${payload.customerName}<br>
        <strong>Table:</strong> #${reservation.table.tableNumber} (${reservation.table.location})<br>
        <strong>Date:</strong> ${formatDate(reservation.reservationDate)}<br>
        <strong>Time:</strong> ${formatTime(reservation.reservationTime)}<br>
        <strong>Guests:</strong> ${reservation.partySize}<br>
        <strong>Status:</strong> ${reservation.status}
    `;
    document.getElementById('success-modal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('success-modal').style.display = 'none';
}


// ===== Toast Notifications =====

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    
    const icons = { success: '✓', error: '✕', info: 'ℹ' };
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<span>${icons[type] || 'ℹ'}</span><span>${message}</span>`;
    
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'toastOut 0.3s ease-in forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}


// ===== Utility Functions =====

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', { 
        weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' 
    });
}

function formatTime(timeStr) {
    if (!timeStr) return '';
    const [hours, minutes] = timeStr.split(':');
    const h = parseInt(hours);
    const period = h >= 12 ? 'PM' : 'AM';
    const displayHour = h % 12 || 12;
    return `${displayHour}:${minutes} ${period}`;
}
