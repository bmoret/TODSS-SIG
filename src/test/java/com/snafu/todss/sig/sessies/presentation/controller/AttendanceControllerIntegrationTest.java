package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.security.config.SecurityConfig;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static com.snafu.todss.sig.sessies.domain.AttendanceState.NO_SHOW;
import static com.snafu.todss.sig.sessies.domain.AttendanceState.PRESENT;
import static com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN;
import static com.snafu.todss.sig.sessies.domain.person.enums.Role.EMPLOYEE;
import static com.snafu.todss.sig.sessies.domain.person.enums.Role.MANAGER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class AttendanceControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SpringPersonRepository PERSON_REPOSITORY;
    @Autowired
    private SessionRepository SESSION_REPOSITORY;
    @Autowired
    private SpecialInterestGroupRepository SIG_REPOSITORY;
    @Autowired
    private SpringAttendanceRepository ATTENDANCE_REPOSITORY;

    private Attendance attendance;

    @BeforeEach
    void setup() {
        ATTENDANCE_REPOSITORY.deleteAll();
        SESSION_REPOSITORY.deleteAll();
        SIG_REPOSITORY.deleteAll();
        PERSON_REPOSITORY.deleteAll();

        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("t_a");
        pb.setFirstname("t");
        pb.setLastname("a");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(VIANEN);
        pb.setRole(MANAGER);
        Person person = PERSON_REPOSITORY.save(pb.build());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SpecialInterestGroup sig = SIG_REPOSITORY.save(new SpecialInterestGroup("name", null, new ArrayList<>(), new ArrayList<>()));
        Session session = SESSION_REPOSITORY.save(
                new PhysicalSession(
                        new SessionDetails(now, nowPlusOneHour, subject, description),
                        SessionState.DRAFT,
                        sig,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        address,
                        null
                )
        );
        attendance = ATTENDANCE_REPOSITORY.save(new Attendance(PRESENT, true, person, session));
    }

    @AfterEach
    void tearDown() {
        ATTENDANCE_REPOSITORY.deleteAll();
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("get attendance by id as manager")
    void getAttendanceByIdAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/attendances/{id}", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(true))
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("get attendance by id as administrator")
    void getAttendanceByIdAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/attendances/{id}", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(true))
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("get attendance by id as employee is not allowed")
    void getAttendanceByIdAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/attendances/{id}", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("get attendance by id as manager throws when attendance is not found")
    void getUnknownAttendanceByIdAsManagerThrows() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(
                get("/attendances/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("update speaker of attendance as manager")
    void updateSpeakerAttendanceAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/update", attendance.getId())
                .content("{\"speaker\":\"true\", \"state\":\"PRESENT\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").exists())
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(true))
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("update speaker of attendance as administrator")
    void updateSpeakerAttendanceAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/update", attendance.getId())
                .content("{\"speaker\":\"true\", \"state\":\"PRESENT\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").exists())
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(true))
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("update speaker of attendance as employee is not allowed")
    void updateSpeakerAttendanceAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/speaker", attendance.getId())
                .content("{\"speaker\":\"true\", \"state\":\"PRESENT\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("update attendance as manager throws when attendance not found")
    void updateSpeakerOfUnknownAttendanceAsManager_ThrowsNotFound() throws Exception{
        UUID id = UUID.randomUUID();
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/update", id)
                .content("{\"speaker\":\"true\", \"state\":\"PRESENT\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("update state of attendance as manager")
    void updateStateAttendanceAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/update", attendance.getId())
                .content("{\"state\":\"NO_SHOW\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("update state of attendance as manager throws when attendance not found")
    void updateStateOfUnknownAttendanceAsManagerThrowsNotFound() throws Exception{
        UUID id = UUID.randomUUID();
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/update", id)
                .content("{\"state\":\"NO_SHOW\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("update presence of attendance when attendee is not present as manager")
    void updatePresenceToNoShowAsManager() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", false);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("update presence of attendance when attendee is not present as secretary")
    void updatePresenceToNoShowAsSecretary() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", false);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("update presence of attendance when attendee is not present as organizer")
    void updatePresenceToNoShowAsOrganizer() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", false);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("update presence of attendance when attendee is not present as administrator")
    void updatePresenceToNoShowAsAdministrator() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", false);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("update presence of attendance when attendee is not present as employee")
    void updatePresenceToNoShowAsEmployee() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", false);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("update presence of attendance when attendee is present as manager")
    void updatePresenceToPresentAsManager() throws Exception {
        attendance.setState(NO_SHOW);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", true);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("update presence of attendance when attendee is present as secretary")
    void updatePresenceToPresentAsSecretary() throws Exception {
        attendance.setState(NO_SHOW);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", true);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("update presence of attendance when attendee is present as organizer")
    void updatePresenceToPresentAsOrganizer() throws Exception {
        attendance.setState(NO_SHOW);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", true);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("update presence of attendance when attendee is present as administrator")
    void updatePresenceToPresentAsAdministrator() throws Exception {
        attendance.setState(NO_SHOW);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", true);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("update presence of attendance when attendee is present as employee")
    void updatePresenceToPresentAsEmployee() throws Exception {
        attendance.setState(NO_SHOW);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isPresent", true);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/attendances/{id}/presence", attendance.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("delete attendance as manager")
    void deleteAttendanceAsManager() throws Exception {
        mockMvc.perform(
                delete("/attendances/{id}", attendance.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("delete attendance as administrator")
    void deleteAttendanceAsAdministrator() throws Exception {
        mockMvc.perform(
                delete("/attendances/{id}", attendance.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("delete attendance as employee is not allowed")
    void deleteAttendanceAsEmployee() throws Exception {
        mockMvc.perform(
                delete("/attendances/{id}", attendance.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("get speakers of attendance as manager")
    void getSpeakersAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/attendances/{id}/speaker", attendance.getSession().getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("get speakers of attendance as administrator")
    void getSpeakersAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/attendances/{id}/speaker", attendance.getSession().getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("get speakers of attendance as employee")
    void getSpeakersAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/attendances/{id}/speaker", attendance.getSession().getId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Check if person is attending   when person is attending")
    void personIsAttendingCheck() throws Exception {
        UUID sessionId = attendance.getSession().getId();
        UUID personId = attendance.getPerson().getId();
        RequestBuilder request = MockMvcRequestBuilders.get("/attendances/"+sessionId+"/"+personId);

        mockMvc.perform(request)
                .andExpect(content().string("false"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Check if person is attending when person is not attending")
    void personIsNotAttendingCheck() throws Exception {
        UUID sessionId = attendance.getSession().getId();
        UUID personId = UUID.randomUUID();
        RequestBuilder request = MockMvcRequestBuilders.get("/attendances/"+sessionId+"/"+personId);

        mockMvc.perform(request)
                .andExpect(content().string("false"))
                .andExpect(status().isOk());
    }

    private Person saveSecondTestPerson() {
        PersonBuilder pb = new PersonBuilder();
        pb.setEmail("b_a");
        pb.setFirstname("b");
        pb.setLastname("c");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(VIANEN);
        pb.setRole(EMPLOYEE);
        return PERSON_REPOSITORY.save(pb.build());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Attend user when user wasn't attending before")
    void signInForAttendance() throws Exception {
        Person person = saveSecondTestPerson();
        UUID personId = person.getId();
        UUID sessionId = attendance.getSession().getId();
        JSONObject body = new JSONObject();
        body.put("state", "PRESENT");
        body.put("speaker", "true");

        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/"+sessionId+"/"+personId)
                .content(body.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
