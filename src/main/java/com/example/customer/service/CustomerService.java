package com.example.customer.service;

import com.example.customer.exception.CustomerNotFoundException;
import com.example.customer.model.Customer;
import com.example.customer.repository.CustomerRepository;
import io.micrometer.common.util.StringUtils;
import org.apache.catalina.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository repository;

    /**
     * Create customer
     * @param customer Customer
     * @return Customer
     */
    public Customer create(Customer customer) {
        logger.info("Creating customer with email: {}", customer.getEmail());
        if(customer.getName() != null){
            customer.setName(customer.getName().trim().toLowerCase());
        }
        if(customer.getEmail() != null){
            customer.setEmail(customer.getEmail().trim().toLowerCase());
        }
        return repository.save(customer);
    }

    public Optional<Customer> getById(UUID id) {
        logger.debug("Fetching customer by ID: {}", id);
        return repository.findById(id);
    }

    public List<Customer> getByName(String name) {
        logger.debug("Fetching customer by name: {}", name);
        List<Customer> customers = repository.findByName(name.trim().toLowerCase());
        if(CollectionUtils.isEmpty(customers)){
            logger.warn("Customer not found with name: {}", name);
            throw new CustomerNotFoundException("Customer not found");
        }
        return customers;
    }

    public Optional<Customer> getByEmail(String email) {
        logger.debug("Fetching customer by email: {}", email);
        return repository.findByEmail(email.trim().toLowerCase());
    }

    public Customer update(UUID id, Customer updated) {
        return repository.findById(id)
                .map(customer -> {
                    customer.setName(StringUtils.isNotEmpty(updated.getName()) ? updated.getName().trim().toLowerCase() : "");
                    customer.setEmail(StringUtils.isNotEmpty(updated.getEmail()) ? updated.getEmail().trim().toLowerCase() : "");
                    customer.setAnnualSpend(updated.getAnnualSpend());
                    customer.setLastPurchaseDate(updated.getLastPurchaseDate());
                    logger.info("Creating customer with email: {}", customer.getEmail());
                    logger.debug("Found customer, applying updates");
                    return repository.save(customer);
                }).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }

    public void delete(UUID id) {
        logger.info("Deleting customer with ID: {}", id);
        repository.deleteById(id);
    }

    public List<Customer> getAll() {
        List<Customer> customers =  repository.findAll();
        if(CollectionUtils.isEmpty(customers)){
            throw new CustomerNotFoundException("No Customers present in db, please add them to DB.");
        }
        return customers;
    }
}
