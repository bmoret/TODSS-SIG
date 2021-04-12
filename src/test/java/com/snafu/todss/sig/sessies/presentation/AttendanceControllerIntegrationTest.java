package com.snafu.todss.sig.sessies.presentation;

import com.snafu.todss.sig.sessies.application.AttendanceService;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringAttendanceRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.session.Session;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.presentation.dto.request.AttendanceRequest;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.domain.StateAttendance.NO_SHOW;
import static com.snafu.todss.sig.sessies.domain.StateAttendance.PRESENT;
import static com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN;
import static com.snafu.todss.sig.sessies.domain.person.enums.Role.MANAGER;
import static com.snafu.todss.sig.sessies.domain.session.SessionState.PLANNED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private Person person;
    private Session session;
    private Attendance attendance;

    @BeforeAll
    static void init() {
    }

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
        person = PERSON_REPOSITORY.save(pb.build());

        SessionDetails sd = new SessionDetails(LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Subject",
                "Description",
                "Vianen",
                false);
        SpecialInterestGroup sig =  this.SIG_REPOSITORY.save(new SpecialInterestGroup());
        session =  SESSION_REPOSITORY.save(new Session(sd, PLANNED, sig, new ArrayList<>(), new ArrayList<>()));

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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.Error").value("Aanwezigheid met id '"+id+"' bestaat niet."));
    }

    @Test
    @DisplayName("update attendance")
    void updateAttendance() throws Exception {
        JSONObject json = new JSONObject();
        json.put("state", "NO_SHOW");
        json.put("speaker", "true");
        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}", attendance.getId())
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value(NO_SHOW.toString()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.speaker").value(true))
                .andExpect(jsonPath("$.session").exists());
    }

    //todo: fix
//    @Test
//    @DisplayName("update attendance throws when request dto is incorrect")
//    void updateAttendanceThrowsException() throws Exception {
//        JSONObject json = new JSONObject();
//        json.put("state", "dfsdgf");
//        json.put("speaker", "true");
//        System.out.println(json);
//        RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}", UUID.randomUUID())
//                .contentType("application/json")
//                .content(json.toString());
//
//        mockMvc.perform(request)
//                .andExpect(content().contentType("application/json"))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.Error").value("Game with id not found"));
//    }

    @Test
    @DisplayName("update attendance throws when attendance not found")
    void updateAttendanceThrowsNotFound() throws Exception{
        UUID id = UUID.randomUUID();
        JSONObject json = new JSONObject();
        json.put("state", "NO_SHOW");
        json.put("speaker", "true");
                RequestBuilder request = MockMvcRequestBuilders.put("/attendances/{id}", id)
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isConflict())
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
