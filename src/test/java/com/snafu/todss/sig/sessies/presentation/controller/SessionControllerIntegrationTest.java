package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.domain.AttendanceState.PRESENT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SessionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private SessionRepository repository;

    @Autowired
    private SpecialInterestGroupRepository sigRepository;

    @Autowired
    private SpringPersonRepository personRepository;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringAttendanceRepository attendanceRepository;

    private Person supervisor;
    private SpecialInterestGroup sig;
    private Attendance attendance;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
    private final String subject = "Subject";
    private final String description = "Description";
    private final String address = "Address";

    @BeforeEach
    void beforeEach() throws NotFoundException {

        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test2@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2021-12-01";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);

        userRepository.save(new User("TestUser", "password", supervisor));

        sig = sigRepository.save(
                new SpecialInterestGroup(
                        "name",
                        null,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        Session session = repository.save(
                new PhysicalSession(
                        new SessionDetails(nowPlusOneHour.plusMonths(2), now.plusMonths(2), subject, description),
                        SessionState.PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                        null
                )
        );

        Session session1 = repository.save(
                new PhysicalSession(
                        new SessionDetails(LocalDateTime.now().minusMonths(2), nowPlusOneHour, subject, description),
                        SessionState.PLANNED,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                        null
                )
        );
        attendance = attendanceRepository.save(new Attendance(PRESENT, true, supervisor, session));
        Attendance attendance1 = attendanceRepository.save(new Attendance(PRESENT, true, supervisor, session1));

        supervisor.addAttendance(attendance);
        session.addAttendee(attendance);

        supervisor.addAttendance(attendance1);
        session1.addAttendee(attendance1);
    }

    @AfterEach
    void tearDown() {
        this.attendanceRepository.deleteAll();
        this.repository.deleteAll();
        this.sigRepository.deleteAll();
        this.userRepository.deleteAll();
        this.personRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get all sessions returns empty list")
    void getAllSessionsWithNoSessions() throws Exception {
        this.attendanceRepository.deleteAll();
        repository.deleteAll();

        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get session by id with not existing session throws Not Found")
    void getSessionByIdWithNotExistingSession_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
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
                .get("/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id").value(session.getId().toString()))
                .andExpect(jsonPath("$.state").value(session.getState().toString()))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(session.getDetails().getSubject()))
                .andExpect(jsonPath("$.details.description").value(session.getDetails().getDescription()))
                .andExpect(jsonPath("$.address").value(session.getAddress()))
                .andExpect(jsonPath("$.contactPerson").exists())
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Create physical session as manager returns the session")
    void createPhysicalSessionAsManager_ReturnsSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.type").value("PHYSICAL"))
                .andExpect(jsonPath("$.contactPerson").exists())
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Create physical session as secretary returns the session")
    void createPhysicalSessionAsSecretary_ReturnsSession() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject1";
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.type").value("PHYSICAL"))
                .andExpect(jsonPath("$.contactPerson").exists())
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("Create physical session as organizer returns the session")
    void createPhysicalSessionAsOrganizer_ReturnsSession() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject2";
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.type").value("PHYSICAL"))
                .andExpect(jsonPath("$.contactPerson").exists())
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Create physical session as administrator returns the session")
    void createPhysicalSessionAsAdministrator_ReturnsSession() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject2";
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.type").value("PHYSICAL"))
                .andExpect(jsonPath("$.contactPerson").exists())
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Create physical session as employee is not allowed")
    void createPhysicalSessionAsEmployee() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @MethodSource("provideOnlineRequests")
    @DisplayName("Create online session as manager returns the session")
    void createOnlineSessionAsManager_ReturnsSession(String platform, String joinUrl, String expectedPlatform, String expectedType) throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.platform").value(expectedPlatform))
                .andExpect(jsonPath("$.joinUrl").value(joinUrl))
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.contactPerson").exists())
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    static Stream<Arguments> provideOnlineRequests() {
        return Stream.of(
                Arguments.of("Platform", "joinUrl", "Platform", "ONLINE"),
                Arguments.of("Teams", "joinUrl", "Teams", "TEAMS")
        );
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Update physical session as manager updates the session")
    void updatePhysicalSessionAsManager_ReturnsUpdatedSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Update physical session as secretary updates the session")
    void updatePhysicalSessionASecretary_ReturnsUpdatedSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("Update physical session as organizer updates the session")
    void updatePhysicalSessionAsOrganizer_ReturnsUpdatedSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Update physical session as administrator updates the session")
    void updatePhysicalSessionAsAdministrator_ReturnsUpdatedSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(subject))
                .andExpect(jsonPath("$.details.description").value(description))
                .andExpect(jsonPath("$.address").value(address))
                .andExpect(jsonPath("$.attendanceInfo").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Update physical session as employee is not allowed")
    void updatePhysicalSessionAsEmployee() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @MethodSource("provideUpdateOnlineRequests")
    @DisplayName("Update online session as manager returns the updated session")
    void updateOnlineSessionAsManager_ReturnsUpdatedSession(String platform, String joinUrl, String expectedPlatform, Session session) throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("DRAFT"))
                .andExpect(jsonPath("$.details.startDate").exists())
                .andExpect(jsonPath("$.details.endDate").exists())
                .andExpect(jsonPath("$.details.subject").value(session.getDetails().getSubject()))
                .andExpect(jsonPath("$.details.description").value(session.getDetails().getDescription()))
                .andExpect(jsonPath("$.platform").value(expectedPlatform))
                .andExpect(jsonPath("$.joinUrl").value(joinUrl))
                .andExpect(jsonPath("$.attendanceInfo").exists());
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
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Deleting a session as manager returns OK without body")
    void deleteSessionAsManager_ReturnsOkWithoutBody() throws Exception {
        Session session = this.repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Deleting a session as secretary returns OK without body")
    void deleteSessionAsSecretary_ReturnsOkWithoutBody() throws Exception {
        Session session = this.repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("Deleting a session as organizer returns OK without body")
    void deleteSessionAsOrganizer_ReturnsOkWithoutBody() throws Exception {
        Session session = this.repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Deleting a session as administrator returns OK without body")
    void deleteSessionAsAdministrator_ReturnsOkWithoutBody() throws Exception {
        Session session = this.repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Deleting a session as employee is not allowed")
    void deleteSessionAsEmployee() throws Exception {
        Session session = this.repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Deleting a non existing session as manager throws not found")
    void deleteNonExistingSessionAsManager_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sessions/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Requesting planning for non existing session as manager throws not found")
    void requestPlanningForNonExistingSessionAsManager_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + UUID.randomUUID() + "/request")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Requesting planning for non existing session as secretary throws not found")
    void requestPlanningForNonExistingSessionAsSecretary_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sessions/" + UUID.randomUUID() + "/request")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Requesting planning for a session that is in wrong state as manager throws IAE")
    void requestPlanningForSessionAsManager_WhenWrongState() throws Exception {
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
                .put("/sessions/" + session.getId() + "/request")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Requesting planning for a session as manager UpdatesState to TO_BE_PLANNED")
    void requestPlanningForSessionAsManager_UpdatesState() throws Exception {
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
                .put("/sessions/" + session.getId() + "/request")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("TO_BE_PLANNED"));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Requesting planning for a session as employee is not allowed")
    void requestPlanningForSessionAsEmployee() throws Exception {
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
                .put("/sessions/" + session.getId() + "/request")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Requesting planning for a session as administrator UpdatesState to TO_BE_PLANNED")
    void requestPlanningForSessionAsAdministrator_UpdatesState() throws Exception {
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
                .put("/sessions/" + session.getId() + "/request")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("TO_BE_PLANNED"));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Plan session returns as manager OK with body")
    void planSessionAsManager_ReturnsOkWithBody() throws Exception {
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
                        LocalDateTime.now().plusHours(2)))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Plan session returns as secretary OK with body")
    void planSessionAsSecretary_ReturnsOkWithBody() throws Exception {
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
                        LocalDateTime.now().plusHours(2)))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Plan session returns as administrator OK with body")
    void planSessionAsAdministrator_ReturnsOkWithBody() throws Exception {
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
                        LocalDateTime.now().plusHours(2)))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Plan session returns as employee is not allowed")
    void planSessionAsEmployee() throws Exception {
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
                        LocalDateTime.now().plusHours(2)))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @MethodSource("ProvideInvalidDateCombinations")
    @DisplayName("Plan session with missing date time arguments as manager is conflict")
    void planSessionWithMissingArgsAsManager_IsConflict(String startDate, String endDate) throws Exception {
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
                        endDate))
                .contentType(MediaType.APPLICATION_JSON);

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


    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get all future sessions returns list sessions")
    void getAllFutureSessions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/future")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get all historical sessions returns list sessions")
    void getAllHistoricalSessions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/history")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get all future sessions of person returns list sessions")
    void getAllFutureSessionsOfPerson() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/future/"+ supervisor.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get all past sessions that a user attended returns list sessions as manager")
    void getHistorySessionsOfUserAsManager() throws Exception {
        repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/history/" + supervisor.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Get all past sessions that a user attended returns list sessions as administrator")
    void getHistorySessionsOfUserAsAdministrator() throws Exception {
        repository.save(new PhysicalSession());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sessions/history/" + supervisor.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }
}