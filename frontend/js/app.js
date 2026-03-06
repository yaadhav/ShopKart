const API = "http://localhost:8080";
const BASE = location.pathname.includes("/pages/") ? ".." : ".";

/* ── Auth helpers ─────────────────────────────── */
const getToken = () => localStorage.getItem("token");
const getUser  = () => JSON.parse(localStorage.getItem("user") || "null");
const isLoggedIn = () => !!getToken();

function saveAuth(data) {
    localStorage.setItem("token", data.token);
    localStorage.setItem("user", JSON.stringify({
        name: data.name,
        email: data.email,
        role: data.role
    }));
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = BASE + "/index.html";
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = BASE + "/pages/login.html";
        return false;
    }
    return true;
}

function redirectIfLoggedIn() {
    if (isLoggedIn()) window.location.href = BASE + "/pages/products.html";
}

/* ── Nav ──────────────────────────────────────── */
function updateNav() {
    const links = document.getElementById("auth-links");
    if (!links) return;

    const user = getUser();
    if (user) {
        const roleLabel = (user.role === "admin" || user.role === "owner")
            ? `<span class="nav-role"> · ${user.role}</span>` : "";
        links.innerHTML =
            `<a href="${BASE}/pages/products.html">Products</a>` +
            `<a href="${BASE}/pages/account.html" class="nav-user">${user.name}${roleLabel}</a>`;
    } else {
        links.innerHTML =
            `<a href="${BASE}/pages/products.html">Products</a>` +
            `<a href="${BASE}/pages/login.html" class="nav-link guest-link">Guest</a>`;
    }
}

/* ── API ──────────────────────────────────────── */
async function apiCall(path, options = {}) {
    const token = getToken();
    const headers = { "Content-Type": "application/json", ...options.headers };
    if (token) headers["Authorization"] = "Bearer " + token;

    const res = await fetch(API + path, { ...options, headers });

    if (res.status === 401 && !path.startsWith("/auth/")) {
        logout();
        return;
    }

    const text = await res.text();

    if (!res.ok) {
        const err = text ? JSON.parse(text) : { message: "Request failed" };
        throw new Error(err.message || `Error ${res.status}`);
    }

    return text ? JSON.parse(text) : null;
}

/* ── DOM helpers ──────────────────────────────── */
function showMsg(id, text, type) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = text;
    el.className = "msg" + (type ? ` msg--${type}` : "");
}

function clearMsg(id) {
    showMsg(id, "", "");
}

function populateSelect(id, items) {
    const select = document.getElementById(id);
    if (!select) return;
    items.forEach(item => {
        const opt = document.createElement("option");
        opt.value = item.name;
        opt.textContent = item.display_name;
        select.appendChild(opt);
    });
}

/* ── Init ─────────────────────────────────────── */
document.addEventListener("DOMContentLoaded", updateNav);
