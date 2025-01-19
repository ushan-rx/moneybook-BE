package com.moneybook.mappers;

import com.moneybook.dto.transaction.MutualTransCreateDto;
import com.moneybook.dto.transaction.MutualTransactionDto;
import com.moneybook.model.MutualTransaction;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MutualTransactionMapper {
    MutualTransactionMapper MAPPER = Mappers.getMapper(MutualTransactionMapper.class);

    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "transactionID", ignore = true)
    @Mapping(target = "transactionDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "expiryDate", expression = "java(java.time.OffsetDateTime.now().plusHours(24))") // Auto-generate expiry
    @Mapping(target = "otpHash", ignore = true)
    MutualTransaction toMutualTransaction(MutualTransCreateDto mutualTransCreateDto);

    @Mapping(target = "qrPayload", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @InheritConfiguration
    MutualTransactionDto fromMutualTransaction(MutualTransaction mutualTransaction);
}
