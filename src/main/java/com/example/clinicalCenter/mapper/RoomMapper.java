package com.example.clinicalCenter.mapper;

import com.example.clinicalCenter.dto.RoomDTO;
import com.example.clinicalCenter.dto.RoomTimeDTO;
import com.example.clinicalCenter.model.Room;
import com.example.clinicalCenter.model.RoomType;

import java.util.ArrayList;
import java.util.List;

public class RoomMapper implements MapperInterface<Room, RoomDTO> {

    @Override
    public Room toEntity(RoomDTO dto) {

        if (dto.type.equals("ordination")) {
            return new Room(dto.id, dto.name, RoomType.ORDINATION);
        } else {
            return new Room(dto.id, dto.name, RoomType.OPERATION_ROOM);
        }

    }

    @Override
    public RoomDTO toDto(Room entity) {
        String firstLetter = entity.getRoomType().toString().substring(0, 1);
        String other = entity.getRoomType().toString().substring(1).toLowerCase();
        other = other.replace('_', ' ');
        return new RoomDTO(entity.getId(), entity.getName(), firstLetter + other);
    }

    public RoomTimeDTO toRoomTimeDTO(Room entity, String date, String startTime, String endTime) {
        String type = "ordination";
        if (entity.getRoomType() == RoomType.OPERATION_ROOM) {
            type = "operation_room";
        }
        return new RoomTimeDTO(entity.getId(), entity.getName(), type, date, startTime, endTime, true);
    }

    public List<RoomTimeDTO> toRoomTimeDTOList(List<Room> roomList, String date, String startTime, String endTime) {

        List<RoomTimeDTO> dtoList = new ArrayList<>();

        for (Room room : roomList) {
            dtoList.add(this.toRoomTimeDTO(room, date, startTime, endTime));
        }

        return dtoList;
    }
}
