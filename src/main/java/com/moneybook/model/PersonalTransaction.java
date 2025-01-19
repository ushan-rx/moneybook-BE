package com.moneybook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "personal_transaction")
public class PersonalTransaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "transaction_id")
    private UUID transactionId;
    @Column(name = "transaction_name")
    private String transactionName;
    @Column(name = "transaction_type")
    @Pattern(regexp = "^(Income|Expense)$", message = "Transaction type must be either Income or Expense")
    private String transactionType;
    @Column(name = "category")
    private String category;
    @Column(name = "description")
    private String description;
    @Column(name = "transaction_date")
    private String transactionDate;
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal transactionAmount;
    @Column(name = "user_id")
    private String userId;
}


