package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.application.SessionService;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService service;

    @Autowired
    private PersonService personService;

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SpecialInterestGroupRepository sigRepository;

    private Person supervisor;

    @BeforeEach
    void beforeEach() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "email@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "01/01/2021";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);
    }

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
        this.sigRepository.deleteAll();
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
    @DisplayName("Get session by id with not existing session throws Not Found")
    void getSessionByIdWithNotExistingSession_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Get session by id returns the session")
    void getSessionById_ReturnsSession() throws Exception {
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
                        address,
                        supervisor

                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/" + session.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(session.getId().toString()))
                .andExpect(jsonPath("$.state").value(session.getState().toString()))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(session.getDetails().getSubject()))
                .andExpect(jsonPath("$.details.description").value(session.getDetails().getDescription()))
                .andExpect(jsonPath("$.address").value(session.getAddress()))
                .andExpect(jsonPath("$.contactPerson").exists());
    }

    @Test
    @DisplayName("Create physical session returns the session")
    void createPhysicalSession_ReturnsSession() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        JSONObject json = new JSONObject();
        json.put("subject", subject);
        json.put("description", description);
        json.put("address", address);
        json.put("sigId", sig.getId().toString());
        json.put("startDate", now.format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("endDate", nowPlusOneHour.format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("contactPerson", supervisor.getId().toString());
        json.put("@type", "PHYSICAL_SESSION_REQUEST");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/sessions")
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.type").value("PHYSICAL"))
                .andExpect(jsonPath("$.contactPerson").exists());
    }

    @ParameterizedTest
    @MethodSource("provideOnlineRequests")
    @DisplayName("Create online session returns the session")
    void createOnlineSession_ReturnsSession(String platform, String joinUrl, String expectedPlatform, String expectedType) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        JSONObject json = new JSONObject();
        json.put("subject", subject);
        json.put("description", description);
        json.put("platform", platform);
        json.put("joinUrl", joinUrl);
        json.put("sigId", sig.getId().toString());
        json.put("startDate", now.format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("endDate", nowPlusOneHour.format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("contactPerson", supervisor.getId().toString());
        json.put("@type", "ONLINE_SESSION_REQUEST");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/sessions")
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.platform").value(expectedPlatform))
                .andExpect(jsonPath("$.joinUrl").value(joinUrl))
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.contactPerson").exists());
    }

    static Stream<Arguments> provideOnlineRequests() {
        return Stream.of(
                Arguments.of("Platform", "joinUrl", "Platform", "ONLINE"),
                Arguments.of("Teams", "joinUrl", "Teams", "TEAMS")
        );
    }

    @Test
    @DisplayName("Update physical session updates the session")
    void updatePhysicalSession_ReturnsUpdatedSession() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        Session session = repository.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "address",
                        supervisor
                )
        );

        JSONObject json = new JSONObject();
        json.put("subject", subject);
        json.put("description", description);
        json.put("address", address);
        json.put("sigId", sig.getId().toString());
        json.put("startDate", now.format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("endDate", nowPlusOneHour.format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("@type", "PHYSICAL_SESSION_REQUEST");
        json.put("contactPerson", null);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + session.getId())
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address));
    }

    @ParameterizedTest
    @MethodSource("provideUpdateOnlineRequests")
    @DisplayName("Update online session returns the updated session")
    void updateOnlineSession_ReturnsUpdatedSession(String platform, String joinUrl, String expectedPlatform, Session session) throws Exception {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        session = repository.save(session);
        JSONObject json = new JSONObject();
        json.put("subject", session.getDetails().getSubject());
        json.put("description", session.getDetails().getDescription());
        json.put("platform", platform);
        json.put("joinUrl", joinUrl);
        json.put("sigId", sig.getId().toString());
        json.put("startDate", session.getDetails().getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("endDate", session.getDetails().getEndDate().format(DateTimeFormatter.ISO_DATE_TIME));
        json.put("contactPerson", supervisor.getId().toString());
        json.put("@type", "ONLINE_SESSION_REQUEST");
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + session.getId())
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(session.getDetails().getSubject()))
                .andExpect(jsonPath("$.details.description").value(session.getDetails().getDescription()))
                .andExpect(jsonPath("$.platform").value(expectedPlatform))
                .andExpect(jsonPath("$.joinUrl").value(joinUrl));
    }
    static Stream<Arguments> provideUpdateOnlineRequests() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String joinUrl = "joinUrl";
        return Stream.of(
                Arguments.of(
                        "Platform",
                        "joinUrl",
                        "Platform",
                        new OnlineSession(
                                new SessionDetails(now, nowPlusOneHour, subject, description),
                                SessionState.DRAFT,
                                null,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                "Platform",
                                joinUrl,
                                null
                        )
                ),
                Arguments.of(
                        "Teams",
                        "joinUrl",
                        "Teams",
                        new TeamsOnlineSession(
                                new SessionDetails(now, nowPlusOneHour, subject, description),
                                SessionState.DRAFT,
                                null,
                                new ArrayList<>(),
                                new ArrayList<>(),
                                joinUrl,
                                null
                        )
                )
        );
    }

    @Test
    @DisplayName("Deleting a session returns OK without body")
    void deleteSession_ReturnsOkWithoutBody() throws Exception {
        Session session = this.repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + session.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Deleting a session that does not exis throws not found")
    void deleteNotExistingSession_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Requesting planning for a session that does not exist throws not found")
    void requestPlanningForNotExistingSession_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + UUID.randomUUID() + "/request");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Requesting planning for a session that when in wrong state throws IAE")
    void requestPlanningForSession_WhenWrongState() throws Exception {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        PhysicalSession session = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Subject", "Description"),
                        SessionState.TO_BE_PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "Address",
                        null
                )
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + session.getId() + "/request");

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Requesting planning for a session UpdatesState to TO_BE_PLANNED")
    void requestPlanningForSession_UpdatesState() throws Exception {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        PhysicalSession session = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Subject", "Description"),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "Address",
                        null
                )
        );

        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + session.getId() + "/request");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("TO_BE_PLANNED"));
    }

    @Test
    @DisplayName("Plan session returns OK with body")
    void planSession_ReturnsOkWithBody() throws Exception {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        Session session = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(null, null, "Subject", "Description"),
                        SessionState.TO_BE_PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "Address",
                        null
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .put(String.format("/sessions/%s/plan?startDate=%s&endDate=%s",
                        session.getId(),
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2))
                );

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("ProvideInvalidDateCombinations")
    @DisplayName("Plan session with missing date time arguments is conflict")
    void planSessionWithMissingArgs_IsConflict(String startDate, String endDate) throws Exception {
        SpecialInterestGroup sig = sigRepository.save(new SpecialInterestGroup());
        Session session = this.repository.save(
                new PhysicalSession(
                        new SessionDetails(null, null, "Subject", "Description"),
                        SessionState.TO_BE_PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        "Address",
                        null
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .put(String.format("/sessions/%s/plan?startdDate=%s&endDate=%s",
                        session.getId(),
                        startDate,
                        endDate)
                );

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }
    private static Stream<Arguments> ProvideInvalidDateCombinations() {
        return Stream.of(
                Arguments.of("", LocalDateTime.now().minusHours(2).toString()),
                Arguments.of(LocalDateTime.now().minusHours(1).toString(), ""),
                Arguments.of("", "")
        );
    }
}