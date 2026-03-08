package com.shopkart.user.controller;

import com.shopkart.common.exception.GlobalExceptionHandler;
import com.shopkart.common.exception.ShopKartException;
import com.shopkart.user.service.AdminService;
import com.shopkart.user.util.UserConstants.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserAdminAPI.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class UserAdminAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    void getUserByUserId_withExistingUser_returns200() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.USER_ID, 1L);
        response.put(Keys.NAME, "John Doe");
        response.put(Keys.EMAIL, "john@shopkart.com");
        response.put(Keys.ROLE, "user");
        response.put(Keys.ROLE + Keys.FORMATTED_SUFFIX, "User");
        response.put(Keys.PHONE_NUMBER, "9876543210");
        response.put(Keys.GENDER, "male");
        response.put(Keys.GENDER + Keys.FORMATTED_SUFFIX, "Male");
        response.put(Keys.DATE_OF_BIRTH, LocalDate.of(1995, 6, 15));
        when(adminService.getUserByUserId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@shopkart.com"))
                .andExpect(jsonPath("$.role").value("user"))
                .andExpect(jsonPath("$.role_formatted").value("User"))
                .andExpect(jsonPath("$.phone_number").value("9876543210"))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.gender_formatted").value("Male"));
    }

    @Test
    void getUserByUserId_withNonExistentUser_returns404() throws Exception {
        when(adminService.getUserByUserId(999L))
                .thenThrow(new ShopKartException(HttpStatus.NOT_FOUND, "AUTH_001", "User not found"));

        mockMvc.perform(get("/api/v1/admin/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("AUTH_001"));
    }

    @Test
    void getUserByUserId_withUserWithoutDetails_returns200WithBasicFields() throws Exception {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(Keys.USER_ID, 2L);
        response.put(Keys.NAME, "Admin User");
        response.put(Keys.EMAIL, "admin@shopkart.com");
        response.put(Keys.ROLE, "admin");
        response.put(Keys.ROLE + Keys.FORMATTED_SUFFIX, "Admin");
        when(adminService.getUserByUserId(2L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(2))
                .andExpect(jsonPath("$.name").value("Admin User"))
                .andExpect(jsonPath("$.role").value("admin"))
                .andExpect(jsonPath("$.role_formatted").value("Admin"))
                .andExpect(jsonPath("$.phone_number").doesNotExist())
                .andExpect(jsonPath("$.gender").doesNotExist());
    }
}
