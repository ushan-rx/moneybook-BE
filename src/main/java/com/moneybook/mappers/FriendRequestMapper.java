package com.moneybook.mappers;

import com.moneybook.dto.friend.FriendRequestCreateDto;
import com.moneybook.dto.friend.FriendRequestDto;
import com.moneybook.model.FriendRequest;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendRequestMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FriendRequest toFriendRequest(FriendRequestCreateDto friendRequestCreateDto);

    @InheritConfiguration
    FriendRequestDto fromFriendRequest(FriendRequest friendRequest);


}
