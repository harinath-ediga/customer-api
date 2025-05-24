package com.example.customer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Customer {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    private BigDecimal annualSpend;
    private LocalDate lastPurchaseDate;

    @Transient
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String tier;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getAnnualSpend() { return annualSpend; }
    public void setAnnualSpend(BigDecimal annualSpend) { this.annualSpend = annualSpend; }

    public LocalDate getLastPurchaseDate() { return lastPurchaseDate; }
    public void setLastPurchaseDate(LocalDate lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; }

    public String getTier() {
        if (annualSpend == null) return "Silver";
        LocalDate now = LocalDate.now();
        if (annualSpend.compareTo(BigDecimal.valueOf(10000)) >= 0 &&
            lastPurchaseDate != null &&
            lastPurchaseDate.isAfter(now.minusMonths(6))) {
            return "Platinum";
        } else if (annualSpend.compareTo(BigDecimal.valueOf(1000)) >= 0 &&
            annualSpend.compareTo(BigDecimal.valueOf(10000)) < 0 &&
            lastPurchaseDate != null &&
            lastPurchaseDate.isAfter(now.minusMonths(12))) {
            return "Gold";
        }
        return "Silver";
    }
}
