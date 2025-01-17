package com.moneybook.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "mutual_transaction")
public class MutualTransaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID transactionID;

    @Column(name = "transaction_name")
    private String transactionName;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "transaction_date")
    private OffsetDateTime transactionDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "borrower_id")
    private String borrowerID;

    @Column(name = "lender_id")
    private String lenderID;

    @Column(name = "status")
    private String status;
}
