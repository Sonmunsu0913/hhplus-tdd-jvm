package io.hhplus.tdd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PointIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 포인트_조회() throws Exception {
        // given
        long userId = 10L;
        long amount = 1000L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount)))
            .andExpect(status().isOk());

        // when & then
        mockMvc.perform(get("/point/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(amount));
    }

    @Test
    void 포인트_충전() throws Exception {
        // given
        long userId = 11L;
        long amount = 2000L;

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(amount));
    }

    @Test
    void 포인트_사용() throws Exception {
        // given
        long userId = 12L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("5000"))
            .andExpect(status().isOk());

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("3000"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(2000));
    }

    @Test
    void 포인트_이력_조회() throws Exception {
        // given
        long userId = 13L;

        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("3000"))
            .andExpect(status().isOk());

        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"))
            .andExpect(status().isOk());

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].userId").value(userId))
            .andExpect(jsonPath("$[1].userId").value(userId));
    }

    @Test
    void 포인트_부족시_예외_응답_확인() throws Exception {
        // given
        long userId = 10L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("1000"))
            .andExpect(status().isOk());

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("2000"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("500"))
            .andExpect(jsonPath("$.message").value("에러가 발생했습니다."));
    }

}
