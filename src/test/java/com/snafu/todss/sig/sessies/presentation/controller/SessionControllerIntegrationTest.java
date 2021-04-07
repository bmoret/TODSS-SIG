package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.SessionService;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService service;

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SpecialInterestGroupRepository sigRepository;

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
    }

    @Test
    @DisplayName("Get all sessions returns list sessions")
    void getAllSessions() throws Exception {
        repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @DisplayName("Get all sessions returns empty list")
    void getAllSessionsWithNoSessions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Get session by id returns the session")
    void getSessionById() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        PhysicalSession session = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/"+session.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(session.getId().toString()))
                .andExpect(jsonPath("$.state").value(session.getState().toString()))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(session.getDetails().getSubject()))
                .andExpect(jsonPath("$.details.description").value(session.getDetails().getDescription()))
                .andExpect(jsonPath("$.address").value(session.getAddress()));
    }
    @Test
    @DisplayName("Get session by id with not existing session throws Not Found")
    void getSessionByIdWithNotExistingSession_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

}