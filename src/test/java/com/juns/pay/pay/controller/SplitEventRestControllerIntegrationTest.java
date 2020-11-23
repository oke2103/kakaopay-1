package com.juns.pay.pay.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juns.pay.room.controller.request.CreateRoomRequest;
import com.juns.pay.room.domain.UserRoomDTO;
import com.juns.pay.room.model.Room;
import com.juns.pay.room.service.RoomService;
import com.juns.pay.room.service.UserRoomService;
import com.juns.pay.split.controller.request.CreateSplitEventRequest;
import com.juns.pay.split.controller.response.SplitEventResponse;
import com.juns.pay.split.service.UserSplitEventService;
import com.juns.pay.user.model.User;
import com.juns.pay.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SplitEventRestControllerIntegrationTest extends ControllerTest {


    private User fromUser;
    private User toUser1;
    private User toUser2;
    private String roomIdentifier;
    private Room room;
    private String token;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserRoomService userRoomService;
    @Autowired
    private UserSplitEventService userSplitEventService;
    @Autowired
    private ObjectMapper mapper;

    public SplitEventRestControllerIntegrationTest() {
        this.mapper = new ObjectMapper();
    }

    @Before
    public void setup() {
        this.fromUser = new User(1L, "ju");
        this.toUser1 = new User(2L, "kka");
        this.toUser2 = new User(3L, "co");
        this.userService.registerUser(this.fromUser);
        this.userService.registerUser(this.toUser1);
        this.userService.registerUser(this.toUser2);

        User foundFromUser = this.userService.getUser(this.fromUser.getId());
        assertNotNull(foundFromUser);
        assertEquals(foundFromUser.getId(), this.fromUser.getId());

        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setName("room name");
        this.roomIdentifier = this.roomService.createRoom(this.fromUser, createRoomRequest);
        Room foundRoom = this.roomService.getByIdentifier(this.roomIdentifier);
        assertNotNull(foundRoom);
        this.room = foundRoom;

        UserRoomDTO foundUserRoom = this.userRoomService.getUserRoom(foundFromUser, foundRoom);
        assertNotNull(foundUserRoom);

        this.userRoomService.join(this.toUser1, this.room);
        this.userRoomService.join(this.toUser2, this.room);
        UserRoomDTO foundToUserRoom = this.userRoomService.getUserRoom(this.toUser1, foundRoom);
        assertNotNull(foundToUserRoom);
    }

    @Test
    @DisplayName("통합 테스트")
    public void split_receive_history_integration_test() throws Exception {

        CreateSplitEventRequest request = new CreateSplitEventRequest();
        request.setMaxCount(5);
        request.setAmount(10000);
        ResultActions result = this.split(this.fromUser.getId(), this.roomIdentifier, 10000, 5)
            .andExpect(jsonPath("$.resultCode", is(0)))
            .andExpect(jsonPath("$.token", hasLength(3)));

        String content = result.andReturn().getResponse().getContentAsString();
        SplitEventResponse response = this.mapper.readValue(content, SplitEventResponse.class);
        log.info(response.getToken());
        this.token = response.getToken();
        log.info(this.token);

        this.receive(this.toUser1.getId(), this.room.getIdentifier(), this.token)
            .andExpect(jsonPath("$.resultCode", is(0)))
            .andExpect(jsonPath("$.receiveAmount").isNumber());

        this.history(this.fromUser.getId(), this.room.getIdentifier(), this.token)
            .andExpect(jsonPath("$.resultCode", is(0)))
            .andExpect(jsonPath("$.result", not(IsEmptyCollection.empty())))
            .andExpect(jsonPath("$.result.userSplitEvents", not(IsEmptyCollection.empty())));
    }
}
