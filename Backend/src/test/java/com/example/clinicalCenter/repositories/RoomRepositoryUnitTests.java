package com.example.clinicalCenter.repositories;

import com.example.clinicalCenter.model.Room;
import com.example.clinicalCenter.repository.RoomRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.clinicalCenter.constants.RoomConstants.ROOM_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class RoomRepositoryUnitTests {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void testFindChosenRoomReturnsValue() {

        Room room = this.roomRepository.findChosenRoom(ROOM_ID);

        assertEquals(ROOM_ID, room.getId());
    }

}
