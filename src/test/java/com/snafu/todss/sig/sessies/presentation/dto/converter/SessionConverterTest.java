package com.snafu.todss.sig.sessies.presentation.dto.converter;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import com.snafu.todss.sig.sessies.presentation.dto.response.SessionResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class SessionConverterTest {
    private static Session mockSession;
    private static PhysicalSession physicalSession;
    private static OnlineSession onlineSession;
    private static TeamsOnlineSession teamsOnlineSession;
    private static SessionResponse mockSessionResponse;
    private static SessionResponse physicalSessionResponse;
    private static SessionResponse onlineSessionResponse;
    private static SessionResponse teamsOnlineSessionResponse;

    @BeforeAll
    static void setup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        String platform = "Platform";
        String joinUrl = "JoinUrl";
        Person person = mock(Person.class);
        mockSession = mock(Session.class,
                Mockito.withSettings()
                        .useConstructor(
                                new SessionDetails(now, nowPlusOneHour, subject, description),
                                SessionState.DRAFT,
                                new SpecialInterestGroup(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                null
                        )
                        .defaultAnswer(CALLS_REAL_METHODS)
        );
        mockSessionResponse = new SessionResponse();
        mockSessionResponse.setType("UNKNOWN");
        mockSessionResponse.setDetails(mockSession.getDetails());
        mockSessionResponse.setState(mockSession.getState());

        physicalSession = new PhysicalSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                address,
                null
        );
        physicalSessionResponse = new SessionResponse();
        physicalSessionResponse.setType("PHYSICAL");
        physicalSessionResponse.setDetails(physicalSession.getDetails());
        physicalSessionResponse.setState(physicalSession.getState());
        physicalSessionResponse.setAddress(physicalSession.getAddress());

        onlineSession = new OnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                joinUrl,
                platform,
                null
        );
        onlineSessionResponse = new SessionResponse();
        onlineSessionResponse.setType("ONLINE");
        onlineSessionResponse.setDetails(onlineSession.getDetails());
        onlineSessionResponse.setState(onlineSession.getState());
        onlineSessionResponse.setPlatform(onlineSession.getPlatform());
        onlineSessionResponse.setJoinUrl(onlineSession.getJoinURL());

        teamsOnlineSession = new TeamsOnlineSession(
                new SessionDetails(now, nowPlusOneHour, subject, description),
                SessionState.DRAFT,
                new SpecialInterestGroup(),
                new ArrayList<>(),
                new ArrayList<>(),
                joinUrl,
                null
        );
        teamsOnlineSessionResponse = new SessionResponse();
        teamsOnlineSessionResponse.setType("TEAMS");
        teamsOnlineSessionResponse.setDetails(teamsOnlineSession.getDetails());
        teamsOnlineSessionResponse.setState(teamsOnlineSession.getState());
        teamsOnlineSessionResponse.setPlatform(teamsOnlineSession.getPlatform());
        teamsOnlineSessionResponse.setJoinUrl(teamsOnlineSession.getJoinURL());
    }

    @ParameterizedTest
    @MethodSource("provideSessions")
    @DisplayName("Convert session to session response, creates session response with the correct values")
    void convertSessionToResponse(Session session, SessionResponse expectedResponse) {
        SessionResponse sessionResponse = SessionConverter.convertSessionToResponse(session);

        SessionDetails details = session.getDetails();
        SessionDetails expectedDetails = expectedResponse.getDetails();
        assertEquals(expectedDetails.getDescription(), details.getDescription());
        assertEquals(expectedDetails.getSubject(), details.getSubject());
        assertEquals(expectedDetails.getStartDate(), details.getStartDate());
        assertEquals(expectedDetails.getEndDate(), details.getEndDate());
        assertEquals(expectedResponse.getId(), sessionResponse.getId());
        assertEquals(expectedResponse.getState(), sessionResponse.getState());
        assertEquals(expectedResponse.getType(), sessionResponse.getType());
        assertEquals(expectedResponse.getAddress(), sessionResponse.getAddress());
        assertEquals(expectedResponse.getJoinUrl(), sessionResponse.getJoinUrl());
        assertEquals(expectedResponse.getPlatform(), sessionResponse.getPlatform());
    }
    static Stream<Arguments> provideSessions() {
        return Stream.of(
                Arguments.of(mockSession, mockSessionResponse),
                Arguments.of(physicalSession, physicalSessionResponse),
                Arguments.of(onlineSession, onlineSessionResponse),
                Arguments.of(teamsOnlineSession, teamsOnlineSessionResponse)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionsList")
    @DisplayName("Convert session list to session responses, creates list of session responses with the correct values")
    void convertSessionListToResponse(List<Session> sessions, List<SessionResponse> expectedResponses) {
        List<SessionResponse> sessionResponses = SessionConverter.convertSessionListToResponse(sessions);

        for (SessionResponse response : sessionResponses) {
            SessionResponse expectedResponse = expectedResponses.get(sessionResponses.indexOf(response));

            SessionDetails details = response.getDetails();
            SessionDetails expectedDetails = expectedResponse.getDetails();
            assertEquals(expectedDetails.getDescription(), details.getDescription());
            assertEquals(expectedDetails.getSubject(), details.getSubject());
            assertEquals(expectedDetails.getStartDate(), details.getStartDate());
            assertEquals(expectedDetails.getEndDate(), details.getEndDate());
            assertEquals(expectedResponse.getId(), response.getId());
            assertEquals(expectedResponse.getState(), response.getState());
            assertEquals(expectedResponse.getType(), response.getType());
            assertEquals(expectedResponse.getAddress(), response.getAddress());
            assertEquals(expectedResponse.getJoinUrl(), response.getJoinUrl());
            assertEquals(expectedResponse.getPlatform(), response.getPlatform());
        }
    }
    static Stream<Arguments> provideSessionsList() {
        return Stream.of(
                Arguments.of(
                        List.of(mockSession, physicalSession, onlineSession, teamsOnlineSession),
                        List.of(mockSessionResponse, physicalSessionResponse, onlineSessionResponse, teamsOnlineSessionResponse)
                ),
                Arguments.of(
                        List.of(),
                        List.of()
                )
        );
    }
}