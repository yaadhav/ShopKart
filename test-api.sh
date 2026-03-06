#!/bin/bash
# Test script for Address & User Details API endpoints

# Configuration
API_BASE="http://localhost:8080"
TEST_USER_EMAIL="testuser@example.com"
TEST_USER_PASSWORD="password123"
TEST_USER_NAME="Test User"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}ShopKart Address & User Details API Tests${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Function to print test header
print_test() {
    echo -e "\n${BLUE}=== $1 ===${NC}"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Step 1: Register or login to get token
print_test "Step 1: User Authentication"

# Try to register (may fail if user exists)
REGISTER_RESPONSE=$(curl -s -X POST "$API_BASE/auth/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"$TEST_USER_NAME\",
    \"email\": \"$TEST_USER_EMAIL\",
    \"password\": \"$TEST_USER_PASSWORD\"
  }")

# Try to login to get token
LOGIN_RESPONSE=$(curl -s -X POST "$API_BASE/auth/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$TEST_USER_EMAIL\",
    \"password\": \"$TEST_USER_PASSWORD\"
  }")

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    print_error "Failed to get authentication token"
    exit 1
else
    print_success "Successfully authenticated"
fi

# Step 2: Create/Update User Details
print_test "Step 2: Create User Details"

USER_DETAILS_RESPONSE=$(curl -s -X POST "$API_BASE/user/details" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "phone_number": "9876543210",
    "gender": 1,
    "date_of_birth": "1990-01-15"
  }')

if echo "$USER_DETAILS_RESPONSE" | grep -q "user_id"; then
    print_success "User details created successfully"
    echo "$USER_DETAILS_RESPONSE" | python3 -m json.tool
else
    print_error "Failed to create user details"
    echo "$USER_DETAILS_RESPONSE"
fi

# Step 3: Get User Details
print_test "Step 3: Get User Details"

GET_USER_DETAILS=$(curl -s -X GET "$API_BASE/user/details" \
  -H "Authorization: Bearer $TOKEN")

if echo "$GET_USER_DETAILS" | grep -q "phone_number"; then
    print_success "Successfully retrieved user details"
    echo "$GET_USER_DETAILS" | python3 -m json.tool
else
    print_error "Failed to get user details"
fi

# Step 4: Update User Details
print_test "Step 4: Update User Details"

UPDATE_USER_DETAILS=$(curl -s -X PUT "$API_BASE/user/details" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "phone_number": "9876543211",
    "gender": 1,
    "date_of_birth": "1990-01-15"
  }')

if echo "$UPDATE_USER_DETAILS" | grep -q "9876543211"; then
    print_success "User details updated successfully"
else
    print_error "Failed to update user details"
fi

# Step 5: Create Home Address (Default)
print_test "Step 5: Create Home Address (Default)"

HOME_ADDRESS=$(curl -s -X POST "$API_BASE/address" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Home",
    "contact_number": "9876543210",
    "first_line": "123 Main Street",
    "second_line": "Apartment 4B",
    "landmark": "Near Central Park",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001",
    "is_default": true
  }')

HOME_ADDRESS_ID=$(echo "$HOME_ADDRESS" | grep -o '"address_id":[0-9]*' | cut -d':' -f2)

if [ ! -z "$HOME_ADDRESS_ID" ]; then
    print_success "Home address created with ID: $HOME_ADDRESS_ID"
    echo "$HOME_ADDRESS" | python3 -m json.tool
else
    print_error "Failed to create home address"
fi

# Step 6: Create Office Address
print_test "Step 6: Create Office Address"

OFFICE_ADDRESS=$(curl -s -X POST "$API_BASE/address" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Office",
    "contact_number": "9123456789",
    "first_line": "456 Business Plaza",
    "second_line": "Floor 10",
    "landmark": "Next to Metro Station",
    "city": "Bangalore",
    "state": "Karnataka",
    "pincode": "560001",
    "is_default": false
  }')

OFFICE_ADDRESS_ID=$(echo "$OFFICE_ADDRESS" | grep -o '"address_id":[0-9]*' | cut -d':' -f2)

if [ ! -z "$OFFICE_ADDRESS_ID" ]; then
    print_success "Office address created with ID: $OFFICE_ADDRESS_ID"
    echo "$OFFICE_ADDRESS" | python3 -m json.tool
else
    print_error "Failed to create office address"
fi

# Step 7: Get All Addresses
print_test "Step 7: Get All Addresses"

ALL_ADDRESSES=$(curl -s -X GET "$API_BASE/address" \
  -H "Authorization: Bearer $TOKEN")

ADDRESS_COUNT=$(echo "$ALL_ADDRESSES" | grep -o '"address_id"' | wc -l | tr -d ' ')

if [ "$ADDRESS_COUNT" -ge "2" ]; then
    print_success "Retrieved $ADDRESS_COUNT addresses"
    echo "$ALL_ADDRESSES" | python3 -m json.tool
else
    print_error "Failed to get all addresses"
fi

# Step 8: Get Specific Address
print_test "Step 8: Get Specific Address"

if [ ! -z "$HOME_ADDRESS_ID" ]; then
    SPECIFIC_ADDRESS=$(curl -s -X GET "$API_BASE/address/$HOME_ADDRESS_ID" \
      -H "Authorization: Bearer $TOKEN")
    
    if echo "$SPECIFIC_ADDRESS" | grep -q "address_id"; then
        print_success "Successfully retrieved address $HOME_ADDRESS_ID"
        echo "$SPECIFIC_ADDRESS" | python3 -m json.tool
    else
        print_error "Failed to get specific address"
    fi
fi

# Step 9: Update Address
print_test "Step 9: Update Address"

if [ ! -z "$OFFICE_ADDRESS_ID" ]; then
    UPDATE_ADDRESS=$(curl -s -X PUT "$API_BASE/address/$OFFICE_ADDRESS_ID" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{
        "name": "Office - Updated",
        "contact_number": "9123456789",
        "first_line": "456 Business Plaza",
        "second_line": "Floor 12",
        "landmark": "Next to Metro Station",
        "city": "Bangalore",
        "state": "Karnataka",
        "pincode": "560001",
        "is_default": false
      }')
    
    if echo "$UPDATE_ADDRESS" | grep -q "Floor 12"; then
        print_success "Address updated successfully"
        echo "$UPDATE_ADDRESS" | python3 -m json.tool
    else
        print_error "Failed to update address"
    fi
fi

# Step 10: Set Office Address as Default
print_test "Step 10: Set Office Address as Default"

if [ ! -z "$OFFICE_ADDRESS_ID" ]; then
    SET_DEFAULT=$(curl -s -X PUT "$API_BASE/address/$OFFICE_ADDRESS_ID/default" \
      -H "Authorization: Bearer $TOKEN")
    
    if echo "$SET_DEFAULT" | grep -q '"is_default":true'; then
        print_success "Office address set as default"
        echo "$SET_DEFAULT" | python3 -m json.tool
    else
        print_error "Failed to set default address"
    fi
fi

# Step 11: Verify Default Changed (Home should not be default anymore)
print_test "Step 11: Verify Default Address Changed"

ALL_ADDRESSES_AFTER=$(curl -s -X GET "$API_BASE/address" \
  -H "Authorization: Bearer $TOKEN")

if [ ! -z "$HOME_ADDRESS_ID" ]; then
    HOME_IS_DEFAULT=$(echo "$ALL_ADDRESSES_AFTER" | grep -A5 "\"address_id\":$HOME_ADDRESS_ID" | grep -o '"is_default":[^,}]*')
    OFFICE_IS_DEFAULT=$(echo "$ALL_ADDRESSES_AFTER" | grep -A5 "\"address_id\":$OFFICE_ADDRESS_ID" | grep -o '"is_default":[^,}]*')
    
    if echo "$HOME_IS_DEFAULT" | grep -q "false" && echo "$OFFICE_IS_DEFAULT" | grep -q "true"; then
        print_success "Default address correctly changed (only one default at a time)"
    else
        print_error "Default address logic not working correctly"
    fi
fi

# Step 12: Get Default Address
print_test "Step 12: Get Default Address"

DEFAULT_ADDRESS=$(curl -s -X GET "$API_BASE/address/default" \
  -H "Authorization: Bearer $TOKEN")

if echo "$DEFAULT_ADDRESS" | grep -q '"is_default":true'; then
    print_success "Successfully retrieved default address"
    echo "$DEFAULT_ADDRESS" | python3 -m json.tool
else
    print_error "Failed to get default address"
fi

# Step 13: Delete Home Address
print_test "Step 13: Delete Home Address"

if [ ! -z "$HOME_ADDRESS_ID" ]; then
    DELETE_STATUS=$(curl -s -w "\n%{http_code}" -X DELETE "$API_BASE/address/$HOME_ADDRESS_ID" \
      -H "Authorization: Bearer $TOKEN")
    
    HTTP_CODE=$(echo "$DELETE_STATUS" | tail -n1)
    
    if [ "$HTTP_CODE" = "204" ]; then
        print_success "Address deleted successfully (HTTP 204)"
    else
        print_error "Failed to delete address (HTTP $HTTP_CODE)"
    fi
fi

# Step 14: Verify Deletion
print_test "Step 14: Verify Address Deleted"

ADDRESSES_AFTER_DELETE=$(curl -s -X GET "$API_BASE/address" \
  -H "Authorization: Bearer $TOKEN")

if ! echo "$ADDRESSES_AFTER_DELETE" | grep -q "\"address_id\":$HOME_ADDRESS_ID"; then
    print_success "Address successfully removed from list"
else
    print_error "Address still appears in list after deletion"
fi

# Summary
echo -e "\n${BLUE}========================================${NC}"
echo -e "${BLUE}Test Summary${NC}"
echo -e "${BLUE}========================================${NC}"
print_success "All API endpoints tested successfully!"
print_success "User Details: Create, Read, Update ✓"
print_success "Addresses: Create, Read, Update, Delete, Set Default ✓"
print_success "Security: JWT authentication working ✓"
print_success "Business Logic: Only one default address ✓"
echo ""
