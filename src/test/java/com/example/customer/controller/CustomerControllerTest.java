package com.example.customer.controller;

import com.example.customer.exception.CustomerNotFoundException;
import com.example.customer.exception.GlobalExceptionHandler;
import com.example.customer.model.Customer;
import com.example.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Customer customer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler).build();
        objectMapper = new ObjectMapper();
        customerId = UUID.randomUUID();
        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAnnualSpend(BigDecimal.valueOf(1000));
    }

    @Test
    void createCustomer_success() throws Exception {
        when(service.create(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.toString())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(service, times(1)).create(any(Customer.class));
    }

    @Test
    void createCustomer_invalidInput_returnsBadRequest() throws Exception {
        Customer invalidCustomer = new Customer();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCustomer)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any(Customer.class));
    }

    @Test
    void getAllCustomer_success() throws Exception {
        List<Customer> customers = Collections.singletonList(customer);
        when(service.getAll()).thenReturn(customers);

        mockMvc.perform(get("/customers/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(customerId.toString())))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));

        verify(service, times(1)).getAll();
    }

    @Test
    void getCustomerById_success() throws Exception {
        when(service.getById(customerId)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.toString())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(service, times(1)).getById(customerId);
    }

    @Test
    void getCustomerById_notFound() throws Exception {
        when(service.getById(customerId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")));

        verify(service, times(1)).getById(customerId);
    }

    @Test
    void getCustomerByName_success() throws Exception {
        String name = "John Doe";
        when(service.getByName(name)).thenReturn(List.of(customer));

        mockMvc.perform(get("/customers").param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(customerId.toString())))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));

        verify(service, times(1)).getByName(name);
    }

    @Test
    void getCustomerByName_notFound() throws Exception {
        String name = "Unknown";
        when(service.getByName(name)).thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(get("/customers").param("name", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")));

        verify(service, times(1)).getByName(name);
    }

    @Test
    void getCustomerByEmail_success() throws Exception {
        String email = "john.doe@example.com";
        when(service.getByEmail(email)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customers").param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.toString())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(service, times(1)).getByEmail(email);
    }

    @Test
    void getCustomerByEmail_notFound() throws Exception {
        String email = "unknown@example.com";
        when(service.getByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers").param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Customer Not Found")))
                .andExpect(jsonPath("$.path", is("/customers")));

        verify(service, times(1)).getByEmail(email);
    }

    @Test
    void updateCustomer_success() throws Exception {
        when(service.update(eq(customerId), any(Customer.class))).thenReturn(customer);

        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.toString())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(service, times(1)).update(eq(customerId), any(Customer.class));
    }

    @Test
    void updateCustomer_notFound() throws Exception {
        when(service.update(eq(customerId), any(Customer.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(put("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")));

        verify(service, times(1)).update(eq(customerId), any(Customer.class));
    }

    @Test
    void deleteCustomer_success() throws Exception {
        doNothing().when(service).delete(customerId);

        mockMvc.perform(delete("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        verify(service, times(1)).delete(customerId);
    }

    @Test
    void deleteCustomer_notFound() throws Exception {
        doThrow(new CustomerNotFoundException("Customer not found")).when(service).delete(customerId);

        mockMvc.perform(delete("/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Customer not found")));

        verify(service, times(1)).delete(customerId);
    }
}