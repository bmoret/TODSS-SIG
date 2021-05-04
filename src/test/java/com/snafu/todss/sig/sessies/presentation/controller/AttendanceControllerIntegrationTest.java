package com.snafu.todss.sig.sessies.presentation.controller;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import static com.snafu.todss.sig.sessies.domain.person.enums.Role.MANAGER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                        address
                )
        );
        attendance = ATTENDANCE_REPOSITORY.save(new Attendance(PRESENT, false, person, session));
    }

    @AfterEach
    void tearDown() {
        ATTENDANCE_REPOSITORY.deleteAll();
    }

    @Test
    @DisplayName("get attendance by id")
    void getAttendanceById() throws Exception {
        mockMvc.perform(
                get("/attendances/{id}", attendance.getId()))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(PRESENT.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(false))
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @DisplayName("get attendance by id throws when game is not found")
    void getAttendanceByIdThrow() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(
                get("/attendances/{id}", id))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @DisplayName("update speaker of attendance")
    void updateSpeakerAttendance() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/speaker", attendance.getId())
                .content("{\"speaker\":\"true\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").exists())
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(true))
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @DisplayName("update attendance throws when attendance not found")
    void updateSpeakerAttendanceThrowsNotFound() throws Exception{
        UUID id = UUID.randomUUID();
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/speaker", id)
                .content("{\"speaker\":\"true\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @DisplayName("update state of attendance")
    void updateStateAttendance() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/state", attendance.getId())
                .content("{\"state\":\"NO_SHOW\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @DisplayName("update state of attendance throws when attendance not found")
    void updateStateAttendanceThrowsNotFound() throws Exception{
        UUID id = UUID.randomUUID();
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}/state", id)
                .content("{\"state\":\"NO_SHOW\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @DisplayName("delete attendance")
    void deleteAttendance() throws Exception {
        mockMvc.perform(
                delete("/attendances/{id}", attendance.getId()))
                .andExpect(status().isNoContent());
    }

}
