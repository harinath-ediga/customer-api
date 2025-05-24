package com.example.customer.controller;

import com.example.customer.exception.CustomerNotFoundException;
import com.example.customer.model.Customer;
import com.example.customer.service.CustomerService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Customer controller for managing customers
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService service;

    @Operation(summary = "Create a new customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public Customer createCustomer(@RequestBody @Valid Customer customer) throws BadRequestException {
        logger.info("Received request to create a new customer");
        if(customer == null){
            throw new BadRequestException("customer info required");
        }
        if(StringUtils.isEmpty(customer.getEmail()) ||
                StringUtils.isEmpty(customer.getName()) ||
                                (customer.getAnnualSpend() == null)){
            throw new BadRequestException("Required Fields are missing");
        }
        return service.create(customer);
    }

    @Operation(summary = "Get all customers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of customers",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/all")
    public List<Customer> getAllCustomer() {
        logger.info("Received request to fetch customer ");
        return service.getAll();
    }

    @Operation(summary = "Get customer by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @GetMapping("/{id}")
    public Customer getCustomerById(
            @Parameter(description = "UUID of the customer to fetch", required = true)
            @PathVariable UUID id) {
        logger.info("Received request to fetch customer by ID: {}", id);
        return service.getById(id).orElseThrow(() -> {
            logger.warn("Customer not found with ID: {}", id);
            return new CustomerNotFoundException("Customer not found");
        });
    }

    @Operation(
            summary = "Find customer by name",
            description = "Returns a customer by their name",
            operationId = "getCustomerByName",
            parameters = {
                    @Parameter(name = "name", description = "Name of the customer", required = false)
            }
    )
    @GetMapping(params = "name")
    public List<Customer> getCustomerByName(
            @Parameter(description = "Name of the customer to fetch", required = false)
            @RequestParam(required = false) String name) {
        logger.info("Received request to fetch customer by name: {}", name);
        return service.getByName(name);
    }

    @Operation(
            summary = "Find customer by name or email",
            description = "Returns a customer by their name or email",
            operationId = "getCustomerByEmail",
            parameters = {
                    @Parameter(name = "email", description = "Email of the customer", required = false)
            }
    )
    @GetMapping(params = "email")
    public Customer getCustomerByEmail(
            @Parameter(description = "Email of the customer to fetch", required = false)
            @RequestParam(required = false) String email) {
        logger.info("Received request to fetch customer by email: {}", email);
        return service.getByEmail(email).orElseThrow(() -> {
            logger.warn("Customer not found with email: {}", email);
            return new CustomerNotFoundException("Customer not found");
        });
    }

    @Operation(summary = "Update a customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PutMapping("/{id}")
    public Customer updateCustomer(
            @Parameter(description = "UUID of the customer to update", required = true)
            @PathVariable UUID id,
            @RequestBody @Valid Customer customer) {
        logger.info("Received request to update customer with ID: {}", id);
        return service.update(id, customer);
    }

    @Operation(summary = "Delete a customer",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Customer deleted"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @Parameter(description = "UUID of the customer to delete", required = true)
            @PathVariable UUID id) {
        logger.info("Received request to delete customer with ID: {}", id);
        service.delete(id);
        return ResponseEntity.ok("success");
    }
}
