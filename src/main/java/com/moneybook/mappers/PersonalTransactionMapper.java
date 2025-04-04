package com.moneybook.mappers;

import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.dto.transaction.PersonalTransactionUpdateDto;
import com.moneybook.model.PersonalTransaction;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonalTransactionMapper {
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "category", source = "category")
    PersonalTransaction toPersonalTransaction(PersonalTransactionCreateDto personalTransactionCreateDto);

    @InheritConfiguration
    PersonalTransactionDto fromPersonalTransaction(PersonalTransaction personalTransaction);


    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    void updatePersonalTransactionFromDto(PersonalTransactionUpdateDto personalTransactionUpdateDto, @MappingTarget PersonalTransaction existingTransaction);
}
