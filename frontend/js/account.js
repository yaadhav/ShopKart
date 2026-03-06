/* ── Account Page Logic ────────────────────────── */

let currentEditingAddressId = null;
let userDetailsLoaded = false;

// Initialize on page load
if (!requireAuth()) { /* redirects */ }

const user = getUser();
if (user) {
    document.getElementById("user-name").textContent = user.name;
    document.getElementById("user-email").textContent = user.email;
    
    // Only show addresses for regular users (role = 1), hide for admin
    if (user.role === 1 || user.role === "user") {
        document.getElementById("addresses-section").style.display = "block";
        loadAddresses();
    } else {
        document.getElementById("addresses-section").style.display = "none";
    }
}

// Load user details
loadUserDetails();

/* ── User Details Functions ──────────────────────── */

async function loadUserDetails() {
    try {
        const data = await apiCall("/user/details");
        userDetailsLoaded = true;
        displayUserDetails(data);
    } catch (err) {
        // User details not found - show edit form button
        console.log("User details not found, user can add them");
        userDetailsLoaded = false;
    }
}

function displayUserDetails(data) {
    const genderMap = { 0: "Not specified", 1: "Male", 2: "Female", 3: "Other" };
    
    // Display phone with +91 prefix (UI only)
    const displayPhone = data.phone_number ? `+91 ${data.phone_number}` : "Not provided";
    document.getElementById("display-phone").textContent = displayPhone;
    document.getElementById("display-gender").textContent = genderMap[data.gender] || "Not specified";
    document.getElementById("display-dob").textContent = data.date_of_birth || "Not provided";
    
    // Update form fields (10-digit part only)
    document.getElementById("phone-number").value = data.phone_number || "";
    document.getElementById("gender").value = data.gender || "0";
    document.getElementById("date-of-birth").value = data.date_of_birth || "";
}

function openUserDetailsForm() {
    document.getElementById("user-details-display").style.display = "none";
    document.getElementById("user-details-form").style.display = "block";
    document.getElementById("edit-details-btn").style.display = "none";
    clearMsg("user-details-msg");
}

function cancelUserDetailsForm() {
    document.getElementById("user-details-display").style.display = "block";
    document.getElementById("user-details-form").style.display = "none";
    document.getElementById("edit-details-btn").style.display = "inline-flex";
    clearMsg("user-details-msg");
}

async function saveUserDetails() {
    clearMsg("user-details-msg");
    
    const phoneNumber = document.getElementById("phone-number").value.trim();
    const gender = parseInt(document.getElementById("gender").value);
    const dateOfBirth = document.getElementById("date-of-birth").value;
    
    // Validate phone number (must be 10 digits, digits only)
    if (phoneNumber && !/^[0-9]{10}$/.test(phoneNumber)) {
        showMsg("user-details-msg", "Phone number must be exactly 10 digits", "error");
        return;
    }
    
    try {
        const requestData = {
            phone_number: phoneNumber || null,  // Send only 10 digits, no +91 prefix
            gender: gender,
            date_of_birth: dateOfBirth || null
        };
        
        const method = userDetailsLoaded ? "PUT" : "POST";
        const data = await apiCall("/user/details", {
            method: method,
            body: JSON.stringify(requestData)
        });
        
        userDetailsLoaded = true;
        displayUserDetails(data);
        cancelUserDetailsForm();
        showMsg("user-details-msg", "Details saved successfully!", "success");
        setTimeout(() => clearMsg("user-details-msg"), 3000);
    } catch (err) {
        showMsg("user-details-msg", err.message, "error");
    }
}

/* ── Address Functions ──────────────────────────── */

async function loadAddresses() {
    try {
        const addresses = await apiCall("/address");
        displayAddresses(addresses);
    } catch (err) {
        // If error is 404 or no addresses, show "No address found"
        document.getElementById("addresses-list").innerHTML = 
            `<p style="text-align: center; color: var(--color-text-secondary); padding: 24px;">No address found</p>`;
    }
}

function displayAddresses(addresses) {
    const container = document.getElementById("addresses-list");
    
    if (!addresses || addresses.length === 0) {
        container.innerHTML = 
            `<p style="text-align: center; color: var(--color-text-secondary); padding: 24px;">No address found</p>`;
        return;
    }
    
    // Sort addresses: default first, then by address_id
    const sortedAddresses = [...addresses].sort((a, b) => {
        if (a.is_default && !b.is_default) return -1;
        if (!a.is_default && b.is_default) return 1;
        return 0;
    });
    
    container.innerHTML = sortedAddresses.map(addr => `
        <div class="address-card" data-id="${addr.address_id}">
            <div class="address-card__header">
                <div>
                    <strong>${addr.name}</strong>
                    ${addr.is_default ? '<span class="badge badge-default">Default</span>' : ''}
                </div>
                <div class="address-card__actions">
                    ${!addr.is_default ? `<button class="btn-icon btn-icon-star" onclick="setDefaultAddress(${addr.address_id})" title="Set as default">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
                        </svg>
                    </button>` : ''}
                    <button class="btn-icon btn-icon-edit" onclick="editAddress(${addr.address_id})" title="Edit">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                    </button>
                    <button class="btn-icon btn-icon-delete" onclick="deleteAddress(${addr.address_id})" title="Delete">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="3 6 5 6 21 6"/>
                            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                            <line x1="10" y1="11" x2="10" y2="17"/>
                            <line x1="14" y1="11" x2="14" y2="17"/>
                        </svg>
                    </button>
                </div>
            </div>
            <div class="address-card__body">
                <p>${addr.first_line}</p>
                <p>${addr.second_line}</p>
                ${addr.landmark ? `<p>${addr.landmark}</p>` : ''}
                <p>${addr.city}, ${addr.state} - ${addr.pincode}</p>
                <p><strong>Contact:</strong> +91 ${addr.contact_number}</p>
            </div>
        </div>
    `).join('');
}

function openAddressModal(addressId = null) {
    currentEditingAddressId = addressId;
    const modal = document.getElementById("address-modal");
    const title = document.getElementById("modal-title");
    
    if (addressId) {
        title.textContent = "Edit Address";
        loadAddressForEdit(addressId);
    } else {
        title.textContent = "Add New Address";
        clearAddressForm();
    }
    
    modal.style.display = "flex";
    clearMsg("address-modal-msg");
}

function closeAddressModal() {
    document.getElementById("address-modal").style.display = "none";
    clearAddressForm();
    currentEditingAddressId = null;
}

function clearAddressForm() {
    document.getElementById("addr-name").value = "";
    document.getElementById("addr-contact").value = "";
    document.getElementById("addr-line1").value = "";
    document.getElementById("addr-line2").value = "";
    document.getElementById("addr-landmark").value = "";
    document.getElementById("addr-city").value = "";
    document.getElementById("addr-state").value = "";
    document.getElementById("addr-pincode").value = "";
    document.getElementById("addr-default").checked = false;
}

async function loadAddressForEdit(addressId) {
    try {
        const addr = await apiCall(`/address/${addressId}`);
        document.getElementById("addr-name").value = addr.name;
        // Contact number is already 10 digits (no +91 prefix from backend)
        document.getElementById("addr-contact").value = addr.contact_number || "";
        document.getElementById("addr-line1").value = addr.first_line;
        document.getElementById("addr-line2").value = addr.second_line;
        document.getElementById("addr-landmark").value = addr.landmark || "";
        document.getElementById("addr-city").value = addr.city;
        document.getElementById("addr-state").value = addr.state;
        document.getElementById("addr-pincode").value = addr.pincode;
        document.getElementById("addr-default").checked = addr.is_default;
    } catch (err) {
        showMsg("address-modal-msg", err.message, "error");
    }
}

async function saveAddress() {
    clearMsg("address-modal-msg");
    
    // Get form values
    const name = document.getElementById("addr-name").value.trim();
    const contactNumber = document.getElementById("addr-contact").value.trim();
    const firstLine = document.getElementById("addr-line1").value.trim();
    const secondLine = document.getElementById("addr-line2").value.trim();
    const landmark = document.getElementById("addr-landmark").value.trim();
    const city = document.getElementById("addr-city").value.trim();
    const state = document.getElementById("addr-state").value.trim();
    const pincode = document.getElementById("addr-pincode").value.trim();
    const isDefault = document.getElementById("addr-default").checked;
    
    // Validate required fields
    if (!name || !contactNumber || !firstLine || !secondLine || !city || !state || !pincode) {
        showMsg("address-modal-msg", "Please fill in all required fields", "error");
        return;
    }
    
    // Validate contact number (must be 10 digits)
    if (!/^[0-9]{10}$/.test(contactNumber)) {
        showMsg("address-modal-msg", "Contact number must be exactly 10 digits", "error");
        return;
    }
    
    // Validate pincode
    if (!/^[0-9]{6}$/.test(pincode)) {
        showMsg("address-modal-msg", "Pincode must be 6 digits", "error");
        return;
    }
    
    const requestData = {
        name,
        contact_number: contactNumber,  // Send only 10 digits, no +91 prefix
        first_line: firstLine,
        second_line: secondLine,
        landmark: landmark || null,
        city,
        state,
        pincode,
        is_default: isDefault
    };
    
    try {
        if (currentEditingAddressId) {
            // Update existing address
            await apiCall(`/address/${currentEditingAddressId}`, {
                method: "PUT",
                body: JSON.stringify(requestData)
            });
        } else {
            // Create new address
            await apiCall("/address", {
                method: "POST",
                body: JSON.stringify(requestData)
            });
        }
        
        closeAddressModal();
        await loadAddresses();
    } catch (err) {
        showMsg("address-modal-msg", err.message, "error");
    }
}

async function editAddress(addressId) {
    openAddressModal(addressId);
}

async function deleteAddress(addressId) {
    if (!confirm("Are you sure you want to delete this address?")) {
        return;
    }
    
    try {
        await apiCall(`/address/${addressId}`, { method: "DELETE" });
        await loadAddresses();
    } catch (err) {
        alert("Failed to delete address: " + err.message);
    }
}

async function setDefaultAddress(addressId) {
    try {
        await apiCall(`/address/${addressId}/default`, { method: "PUT" });
        await loadAddresses();
    } catch (err) {
        alert("Failed to set default address: " + err.message);
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById("address-modal");
    if (event.target === modal) {
        closeAddressModal();
    }
}

// Phone number validation - digits only
document.getElementById("phone-number").addEventListener("input", function(e) {
    e.target.value = e.target.value.replace(/\D/g, '');
});

document.getElementById("addr-contact").addEventListener("input", function(e) {
    e.target.value = e.target.value.replace(/\D/g, '');
});
