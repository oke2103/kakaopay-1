package com.juns.pay.pay.controller;

import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.juns.pay.common.Policy;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.controller.room.request.CreateRoomRequest;
import com.juns.pay.controller.split.request.CreateSplitEventRequest;
import com.juns.pay.controller.split.response.SplitEventResponse;
import com.juns.pay.domain.room.UserRoomDTO;
import com.juns.pay.model.room.Room;
import com.juns.pay.model.user.User;
import com.juns.pay.service.room.RoomService;
import com.juns.pay.service.room.UserRoomService;
import com.juns.pay.service.user.UserService;
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
import org.springframework.test.web.servlet.ResultActions;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SplitEventControllerIntegrationTest extends SplitEventControllerTest {


    private User fromUser;
    private User toUser;
    private String roomIdentifier;
    private Room room;

    @Autowired
    private UserService userService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserRoomService userRoomService;

    @Before
    public void setup() {
        this.fromUser = new User(1L, "ju");
        this.toUser = new User(2L, "kka");
        this.userService.registerUser(this.fromUser);
        this.userService.registerUser(this.toUser);

        User foundFromUser = this.userService.getUser(this.fromUser.getId());
        assert foundFromUser != null;
        assert foundFromUser.getId() == this.fromUser.getId();

        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setName("room name");
        this.roomIdentifier = this.roomService.createRoom(this.fromUser, createRoomRequest);
        Room foundRoom = this.roomService.getByIdentifier(this.roomIdentifier);
        assert foundRoom != null;
        this.room = foundRoom;

        UserRoomDTO foundUserRoom = this.userRoomService.getUserRoom(foundFromUser, foundRoom);
        assert foundUserRoom != null;

        this.userRoomService.join(this.toUser, this.room);
        UserRoomDTO foundToUserRoom = this.userRoomService.getUserRoom(this.toUser, foundRoom);
        assert foundToUserRoom != null;
    }

    @Test
    @DisplayName("기능 통합 테스트")
    public void split_receive_history_integration_test() throws Exception {

        CreateSplitEventRequest request = new CreateSplitEventRequest();
        request.setMaxCount(5);
        request.setAmount(10000);
        ResultActions result = this.split(this.fromUser.getId(), this.roomIdentifier, 10000, 5)
            .andExpect(jsonPath("$.resultCode", is(ResultEnum.OK.getCode())))
            .andExpect(jsonPath("$.token", hasLength(Policy.SPLITEVENT_TOKEN_LENGTH)));

        String content = result.andReturn().getResponse().getContentAsString();
        SplitEventResponse response = this.mapper.readValue(content, SplitEventResponse.class);
        log.info(response.getToken());
        String token = response.getToken();

        this.receive(this.toUser.getId(), this.room.getIdentifier(), token)
            .andExpect(jsonPath("$.resultCode", is(ResultEnum.OK.getCode())))
            .andExpect(jsonPath("$.receiveAmount").isNumber());

        this.history(this.fromUser.getId(), this.room.getIdentifier(), token)
            .andExpect(jsonPath("$.resultCode", is(ResultEnum.OK.getCode())))
            .andExpect(jsonPath("$.result", not(IsEmptyCollection.empty())))
            .andExpect(jsonPath("$.result.userSplitEvents", not(IsEmptyCollection.empty())));
    }
}
