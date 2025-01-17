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

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transactionID", ignore = true)
    MutualTransaction toMutualTransaction(MutualTransCreateDto mutualTransCreateDto);

    @InheritConfiguration
    MutualTransactionDto fromMutualTransaction(MutualTransaction mutualTransaction);
}
