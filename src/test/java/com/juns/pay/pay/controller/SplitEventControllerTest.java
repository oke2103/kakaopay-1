package com.juns.pay.pay.controller;

import static com.juns.pay.pay.ApiDocumentUtils.getDocumentRequest;
import static com.juns.pay.pay.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.juns.pay.common.HttpHeaderKeyDefine;
import com.juns.pay.split.controller.request.CreateSplitEventRequest;
import com.juns.pay.split.domain.SplitEventTokenDTO;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


public class SplitEventControllerTest {


    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private WebApplicationContext context;

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
    @Before
    public void setUp() {

        RestDocumentationResultHandler document = document(
            "{class-name}/{method-name}",
            preprocessResponse(prettyPrint())
        );
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation)
                .uris().withScheme("https").withHost("juns-apis.com").withPort(443))
            .alwaysDo(document)
            .build();
    }

    protected ResultActions split(long userId, String roomId, double amount, int maxCount) throws Exception {
        CreateSplitEventRequest request = new CreateSplitEventRequest();
        request.setAmount(amount);
        request.setMaxCount(maxCount);
        String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(request);
        Map<String, Object> data = new Gson().fromJson(jsonString, Map.class);

        String content = this.mapper.writeValueAsString(data);
        return this.mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/split/randomly")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderKeyDefine.ROOM_ID, roomId)
                .header(HttpHeaderKeyDefine.USER_ID, String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("split-api",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("X-USER-ID").description("사용자 ID"),
                    headerWithName("X-ROOM-ID").description("방 고유 ID")
                ),
                requestFields(
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("금액"),
                    fieldWithPath("maxCount").type(JsonFieldType.NUMBER).description("사용자 수")
                ),
                responseFields(
                    fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과코드"),
                    fieldWithPath("resultMessage").type(JsonFieldType.STRING).description("결과메시지"),
                    fieldWithPath("token").type(JsonFieldType.STRING).description("뿌리기 이벤트 고유 token"),
                    fieldWithPath("detail").type(JsonFieldType.STRING).description("상세 메시지")
                )
            ));
    }

    protected ResultActions receive(long userId, String roomId, String token) throws Exception {
        SplitEventTokenDTO request = new SplitEventTokenDTO(token);
        return this.mockMvc.perform(
            RestDocumentationRequestBuilders.put("/api/v1/split/receive")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderKeyDefine.ROOM_ID, roomId)
                .header(HttpHeaderKeyDefine.USER_ID, String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("receive-api",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("X-USER-ID").description("사용자 ID"),
                    headerWithName("X-ROOM-ID").description("방 고유 ID")
                ),
                requestFields(
                    fieldWithPath("token").type(JsonFieldType.STRING).description("뿌리기 이벤트 고유 token")
                ),
                responseFields(
                    fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과코드"),
                    fieldWithPath("resultMessage").type(JsonFieldType.STRING).description("결과메시지"),
                    fieldWithPath("receiveAmount").type(JsonFieldType.NUMBER).description("받은 금액"),
                    fieldWithPath("detail").type(JsonFieldType.STRING).description("상세 메시지")
                )
            ));

    }

    protected ResultActions history(long userId, String roomId, String token) throws Exception {
        SplitEventTokenDTO request = new SplitEventTokenDTO(token);
        return this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/split/history")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaderKeyDefine.ROOM_ID, roomId)
                .header(HttpHeaderKeyDefine.USER_ID, String.valueOf(userId))
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            /*.andDo(document("history-api",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("X-USER-ID").description("사용자 ID"),
                    headerWithName("X-ROOM-ID").description("방 고유 ID")
                ),
                requestFields(
                    fieldWithPath("token").type(JsonFieldType.STRING).description("뿌리기 이벤트 고유 token")
                ),
                responseFields(
                    fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("resultMessage").type(JsonFieldType.STRING).description("결과 메시지"),
                    fieldWithPath("detail").type(JsonFieldType.STRING).description("상세 메시지")),
                responseFields(beneathPath("result.userSplitEvents"),
                    fieldWithPath("[].timeReceive]").description("timeReceive"),
                    fieldWithPath("[].receiveAmount]").description("receiveAmount")))*/
            ;

    }
}
