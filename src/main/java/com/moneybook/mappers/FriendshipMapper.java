package com.moneybook.mappers;

import com.moneybook.dto.friend.FriendshipCreateDto;
import com.moneybook.model.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    @Mapping(target = "friendshipId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Friendship toFriendship(FriendshipCreateDto friendshipCreateDto);

}
