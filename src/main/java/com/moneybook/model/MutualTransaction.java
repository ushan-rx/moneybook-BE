package com.moneybook.model;

import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.model.enums.TransactionType;
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
    @Column(name = "transaction_id")
    private UUID transactionID;

    @Column(name = "transaction_name", nullable = false)
    private String transactionName;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private OffsetDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // Loan or Borrow

    @Column(name = "borrower_id", nullable = false)
    private String borrowerID;

    @Column(name = "lender_id", nullable = false)
    private String lenderID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status; // Enum for PENDING, ACCEPTED, REJECTED, CANCELLED

    @Column(name = "otp_hash")
    private String otpHash; // Stores the hashed OTP for Redis fallback

    @Column(name = "expiry_date", nullable = false)
    private OffsetDateTime expiryDate; // Tracks when the transaction expires
}
