package com.moneybook.mappers;

import com.moneybook.dto.friend.FriendshipCreateDto;
import com.moneybook.model.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FriendshipMapper {

    FriendshipMapper MAPPER = Mappers.getMapper(FriendshipMapper.class);


    @Mapping(target = "friendshipId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Friendship toFriendship(FriendshipCreateDto friendshipCreateDto);

}
