package com.juns.pay.pay.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.juns.pay.common.HttpHeaderKeyDefine;
import com.juns.pay.split.controller.SplitEventController;
import com.juns.pay.split.controller.request.CreateSplitEventRequest;
import com.juns.pay.split.domain.SplitEventTokenDTO;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


public class ControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private RestDocumentationResultHandler document;

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

//
//    private SplitEventService splitEventService;


    public ControllerTest() {
        this.mapper = new ObjectMapper();
    }

    @Before
    public void setup() {
        // 이곳에서 HomeController를 MockMvc 객체로 만듭니다.
        this.mockMvc = MockMvcBuilders.standaloneSetup(new SplitEventController()).build();
    }

//
//    @Before
//    public void setup() throws Exception {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
//    }

   /* @Before
    public void setUp() {
        this.document = document(
            "{class-name}/{method-name}",
            preprocessResponse(prettyPrint())
        );
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation)
                .uris().withScheme("https").withHost("juns.com").withPort(443))
            .alwaysDo(this.document)
            .build();
    }*/

    /*
     * 뿌리기 API 테스트
     * request
     * @param double amount : 뿌릴 금액
     * @param int maxCount : 받아갈 인원 수
     *
     * response
     * @param double token : 이벤트 고유 토큰
     * {@link com.juns.pay.split.controller.SplitEventController}
     * */

    protected ResultActions split(long userId, String roomId, double amount, int maxCount) throws Exception {
        CreateSplitEventRequest request = new CreateSplitEventRequest();
        request.setAmount(amount);
        request.setMaxCount(maxCount);
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(request);
        Map<String, Object> data = new Gson().fromJson(jsonString, Map.class);

        String content = this.mapper.writeValueAsString(data);
        return this.mockMvc.perform(
            put("/api/v1/split/randomly")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderKeyDefine.ROOM_ID, roomId)
                .header(HttpHeaderKeyDefine.USER_ID, String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions receive(long userId, String roomId, String token) throws Exception {
        SplitEventTokenDTO request = new SplitEventTokenDTO(token);
        return this.mockMvc.perform(
            put("/api/v1/split/receive")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderKeyDefine.ROOM_ID, roomId)
                .header(HttpHeaderKeyDefine.USER_ID, String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }

    protected ResultActions history(long userId, String roomId, String token) throws Exception {
        SplitEventTokenDTO request = new SplitEventTokenDTO(token);

        return this.mockMvc.perform(
            post("/api/v1/split/history")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderKeyDefine.ROOM_ID, roomId)
                .header(HttpHeaderKeyDefine.USER_ID, String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
//
//    @MockBean // (2)
//    private UserService userService;
////    private RestDocumentationResultHandler document;
//
////    @Before
////    public void setUp() {
////        this.document = document(
////            "{class-name}/{method-name}",
////            preprocessResponse(prettyPrint())
////        );
////        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
////            .apply(documentationConfiguration(this.restDocumentation)
////                .uris().withScheme("https").withHost("juns.com").withPort(443))
////            .alwaysDo(this.document)
////            .build();
////    }
//
////    @Test
////    public void whenFindById_thenReturnUser() {
////        // given
////        User user = new User(1L, "jun");
////        this.userService.createUser(user);
////        // when
////        User found = this.userService.getUser(user.getId());
////
////        // then
////        Assertions.assertThat(found)
////            .isEqualTo(user);
////    }
//
//    @Test
//    public void user_sign_up() throws Exception {
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setName("jun");
//        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(userDTO);
//
//        Map<String, Object> data = new Gson().fromJson(jsonString, Map.class);
//
//        ResultActions result = this.mockMvc.perform(
//            put("/api/v1/user/sign-up", 1L)
//                .content(this.objectMapper.writeValueAsString(data))
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//        );
//        result.andExpect(status().isOk())
//            .andDo(document("user_sign_up", // (4)
//                getDocumentRequest(),
//                getDocumentResponse(),
//                pathParameters(
//                    parameterWithName("id").description("아이디")
//                ),
//                requestFields(
//                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
//                ),
//                responseFields(
//                    fieldWithPath("code").type(JsonFieldType.STRING).description("결과코드"),
//                    fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
//                    fieldWithPath("data.person.id").type(JsonFieldType.NUMBER).description("아이디"),
//                    fieldWithPath("data.person.firstName").type(JsonFieldType.STRING).description("이름")
//                )
//            ));
//
//    }
}
