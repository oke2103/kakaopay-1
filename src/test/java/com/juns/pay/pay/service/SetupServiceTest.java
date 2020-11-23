package com.juns.pay.pay.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.juns.pay.room.controller.request.CreateRoomRequest;
import com.juns.pay.room.domain.UserRoomDTO;
import com.juns.pay.room.model.Room;
import com.juns.pay.room.service.RoomService;
import com.juns.pay.room.service.UserRoomService;
import com.juns.pay.split.service.SplitEventService;
import com.juns.pay.split.service.UserSplitEventService;
import com.juns.pay.user.model.User;
import com.juns.pay.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SetupServiceTest {

    protected User fromUser;
    protected User toUser;
    protected User notAttedee;
    protected String roomIdentifier;
    protected Room room;

    @Autowired
    protected UserService userService;
    @Autowired
    protected RoomService roomService;
    @Autowired
    protected UserRoomService userRoomService;
    @Autowired
    protected SplitEventService splitEventService;
    @Autowired
    protected UserSplitEventService userSplitEventService;

    @Before
    public void setup() {
        this.fromUser = new User(1L, "jueun");
        this.toUser = new User(2L, "coco");
        this.notAttedee = new User(3L, "kkami");
        this.userService.registerUser(this.fromUser);
        this.userService.registerUser(this.toUser);
        this.userService.registerUser(this.notAttedee);

        User foundFromUser = this.userService.getUser(this.fromUser.getId());
        assertNotNull(foundFromUser);
        assertEquals(foundFromUser.getId(), this.fromUser.getId());

        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setName("juns room");
        this.roomIdentifier = this.roomService.createRoom(this.fromUser, createRoomRequest);
        Room foundRoom = this.roomService.getByIdentifier(this.roomIdentifier);
        assertNotNull(foundRoom);
        this.room = foundRoom;

        UserRoomDTO foundUserRoom = this.userRoomService.getUserRoom(foundFromUser, foundRoom);
        assertNotNull(foundUserRoom);

        this.userRoomService.join(this.toUser, this.room);
        UserRoomDTO foundToUserRoom = this.userRoomService.getUserRoom(this.toUser, foundRoom);
        assertNotNull(foundToUserRoom);
    }

}
