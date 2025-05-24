package com.example.customer.service;

import com.example.customer.exception.CustomerNotFoundException;
import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    private Customer customer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        customer = new Customer();
        customer.setId(customerId);
        customer.setName("Harinath");
        customer.setEmail("harinath.ediga23@gmail.com");
        customer.setAnnualSpend(BigDecimal.valueOf(1000.0));
        customer.setLastPurchaseDate(LocalDate.of(2025, 5, 1));
    }

    @Test
    void create_success() {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.create(customer);

        assertNotNull(result);
        assertEquals("harinath", result.getName());
        assertEquals("harinath.ediga23@gmail.com", result.getEmail());
        assertEquals(BigDecimal.valueOf(1000.0), result.getAnnualSpend());
        assertEquals(LocalDate.of(2025, 5, 1), result.getLastPurchaseDate());
        verify(repository, times(1)).save(customer);
    }

    @Test
    void create_trimsAndLowercasesNameAndEmail() {
        Customer input = new Customer();
        input.setName("  Harinath  ");
        input.setEmail("  harinath.ediga23@gmail.com  ");
        input.setAnnualSpend(BigDecimal.valueOf(1000.0));
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertEquals("harinath", result.getName());
        assertEquals("harinath.ediga23@gmail.com", result.getEmail());
        verify(repository, times(1)).save(input);
    }

    @Test
    void create_handlesNullNameAndEmail() {
        Customer input = new Customer();
        input.setAnnualSpend(BigDecimal.valueOf(1000.0));
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertNull(result.getName());
        assertNull(result.getEmail());
        verify(repository, times(1)).save(input);
    }
    
    @Test
    void create_withLowSpend_setsBronzeTier() {
        Customer input = new Customer();
        input.setName("Harinath Ediga");
        input.setEmail("harinath.ediga23@gmail.com");
        input.setAnnualSpend(BigDecimal.valueOf(500.0)); // Should set tier to Bronze
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertNotNull(result);
        assertEquals("harinath ediga", result.getName());
        assertEquals("harinath.ediga23@gmail.com", result.getEmail());
        assertEquals(BigDecimal.valueOf(500.0), result.getAnnualSpend());
        assertEquals("Silver", result.getTier());
        verify(repository, times(1)).save(input);
    }

    @Test
    void create_withMediumSpend_setsSilverTier() {
        Customer input = new Customer();
        input.setName("Harinath Ediga");
        input.setEmail("harinath.ediga23@gmail.com");
        input.setAnnualSpend(BigDecimal.valueOf(2000.0)); // Should set tier to Silver
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertEquals(BigDecimal.valueOf(2000.0), result.getAnnualSpend());
        assertEquals("Gold", result.getTier());
        verify(repository, times(1)).save(input);
    }

    @Test
    void create_withHighSpend_setsGoldTier() {
        Customer input = new Customer();
        input.setName("Harinath Ediga");
        input.setEmail("harinath.ediga23@gmail.com");
        input.setAnnualSpend(BigDecimal.valueOf(6000.0)); // Should set tier to Gold
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertEquals(BigDecimal.valueOf(6000.0), result.getAnnualSpend());
        assertEquals("Gold", result.getTier());
        verify(repository, times(1)).save(input);
    }

    @Test
    void create_withNullSpend_setsBronzeTier() {
        Customer input = new Customer();
        input.setName("Harinath Ediga");
        input.setEmail("harinath.ediga23@gmail.com");
        input.setAnnualSpend(null); // Should set tier to Bronze
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertNull(result.getAnnualSpend());
        assertEquals("Silver", result.getTier());
        verify(repository, times(1)).save(input);
    }

    @Test
    void create_withNegativeSpend_setsBronzeTier() {
        Customer input = new Customer();
        input.setName("Harinath Ediga");
        input.setEmail("harinath.ediga23@gmail.com");
        input.setAnnualSpend(BigDecimal.valueOf(-100.0)); // Should set tier to Bronze
        input.setLastPurchaseDate(LocalDate.of(2025, 5, 1));

        when(repository.save(any(Customer.class))).thenReturn(input);

        Customer result = service.create(input);

        assertEquals(BigDecimal.valueOf(-100.0), result.getAnnualSpend());
        assertEquals("Silver", result.getTier());
        verify(repository, times(1)).save(input);
    }

    @Test
    void update_withLowSpend_setsBronzeTier() {
        Customer updated = new Customer();
        updated.setName("Harinath Ediga");
        updated.setEmail("harinath.ediga23@gmail.com");
        updated.setAnnualSpend(BigDecimal.valueOf(500.0)); // Should set tier to Bronze
        updated.setLastPurchaseDate(LocalDate.of(2025, 6, 1));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.update(customerId, updated);

        assertEquals("harinath ediga", result.getName());
        assertEquals("harinath.ediga23@gmail.com", result.getEmail());
        assertEquals(BigDecimal.valueOf(500.0), result.getAnnualSpend());
        assertEquals("Silver", result.getTier());
        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void update_withMediumSpend_setsSilverTier() {
        Customer updated = new Customer();
        updated.setName("Harinath Ediga");
        updated.setEmail("harinath.ediga23@gmail.com");
        updated.setAnnualSpend(BigDecimal.valueOf(2000.0)); // Should set tier to Silver
        updated.setLastPurchaseDate(LocalDate.of(2025, 6, 1));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.update(customerId, updated);

        assertEquals(BigDecimal.valueOf(2000.0), result.getAnnualSpend());
        assertEquals("Gold", result.getTier());
        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void update_withHighSpend_setsGoldTier() {
        Customer updated = new Customer();
        updated.setName("Harinath Ediga");
        updated.setEmail("harinath.ediga23@gmail.com");
        updated.setAnnualSpend(BigDecimal.valueOf(6000.0)); // Should set tier to Gold
        updated.setLastPurchaseDate(LocalDate.of(2025, 6, 1));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.update(customerId, updated);

        assertEquals(BigDecimal.valueOf(6000.0), result.getAnnualSpend());
        assertEquals("Gold", result.getTier());
        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void update_withNullSpend_setsBronzeTier() {
        Customer updated = new Customer();
        updated.setName("Harinath Ediga");
        updated.setEmail("harinath.ediga23@gmail.com");
        updated.setAnnualSpend(null); // Should set tier to Bronze
        updated.setLastPurchaseDate(LocalDate.of(2025, 6, 1));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.update(customerId, updated);

        assertNull(result.getAnnualSpend());
        assertEquals("Silver", result.getTier());
        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void update_withNegativeSpend_setsBronzeTier() {
        Customer updated = new Customer();
        updated.setName("Harinath Ediga");
        updated.setEmail("harinath.ediga23@gmail.com");
        updated.setAnnualSpend(BigDecimal.valueOf(-100.0)); // Should set tier to Bronze
        updated.setLastPurchaseDate(LocalDate.of(2025, 6, 1));

        when(repository.findById(customerId)).thenReturn(Optional.of(customer));
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = service.update(customerId, updated);

        assertEquals(BigDecimal.valueOf(-100.0), result.getAnnualSpend());
        assertEquals("Silver", result.getTier());
        verify(repository, times(1)).findById(customerId);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void update_notFound_throwsException() {
        Customer updated = new Customer();
        when(repository.findById(customerId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
            service.update(customerId, updated);
        });

        assertEquals("Customer not found", exception.getMessage());
        verify(repository, times(1)).findById(customerId);
        verify(repository, never()).save(any(Customer.class));
    }

    @Test
    void getById_success() {
        when(repository.findById(customerId)).thenReturn(Optional.of(customer));

        Optional<Customer> result = service.getById(customerId);

        assertTrue(result.isPresent());
        assertEquals(customerId, result.get().getId());
        assertEquals("Gold", result.get().getTier());
        verify(repository, times(1)).findById(customerId);
    }

    @Test
    void getAll_success() {
        List<Customer> customers = Arrays.asList(customer);
        when(repository.findAll()).thenReturn(customers);

        List<Customer> result = service.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Gold", result.get(0).getTier());
        verify(repository, times(1)).findAll();
    }
}
