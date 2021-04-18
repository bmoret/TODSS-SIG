package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.OnlineSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.domain.session.types.TeamsOnlineSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnlineSessionBuilderTest {
    private OnlineSessionBuilder builder;
    private LocalDateTime now;
    private LocalDateTime nowPlusOneHour;
    private String subject;
    private String description;
    private String joinUrl;

    @BeforeEach
    void setup() {
        builder = new OnlineSessionBuilder();
        now = LocalDateTime.now();
        nowPlusOneHour = LocalDateTime.now().plusHours(1);
        subject = "Subject";
        description = "Description";
        joinUrl = "JoinUrl";
    }

    @Test
    @DisplayName("Default, not set builder returns Session of type PhysicalSession")
    void defaultBuild_ReturnsOnlineSession() {
        Session session = builder.build();

        assertTrue(session instanceof OnlineSession);
    }

    @Test
    @DisplayName("Default, not set builder returns PhysicalSession with default values")
    void defaultBuild_ReturnsOnlineSessionWithValues() {
        OnlineSession session = builder.build();

        assertEquals(SessionState.DRAFT, session.getState());
        assertEquals("", session.getPlatform());
        assertEquals("", session.getJoinURL());
    }

    @ParameterizedTest
    @MethodSource("provideSessionArgs")
    @DisplayName("All set builder with platform input, returns Online with all set attributes and platform")
    void AllSetBuilder_ReturnsOnlineSessionWithValues(String platform, String expectedPlatform) {
        SessionState state = SessionState.DRAFT;
        builder.setStartDate(now);
        builder.setEndDate(nowPlusOneHour);
        builder.setSubject(subject);
        builder.setDescription(description);
        builder.setSig(new SpecialInterestGroup());
        builder.setPlatform(platform);
        builder.setJoinUrl(joinUrl);

        OnlineSession session = builder.build();

        SessionDetails details = session.getDetails();
        assertEquals(now, details.getStartDate());
        assertEquals(nowPlusOneHour, details.getEndDate());
        assertEquals(subject, details.getSubject());
        assertEquals(description, details.getDescription());
        assertEquals(state, session.getState());
        assertEquals(expectedPlatform, session.getPlatform());
        assertEquals(joinUrl, session.getJoinURL());
    }
    static Stream<Arguments> provideSessionArgs() {
        return Stream.of(
                Arguments.of("Random Platform Name", "Random Platform Name"),
                Arguments.of("Teams", "Teams"),
                Arguments.of("TEAMS", "Teams"),
                Arguments.of("tEAMS", "Teams")
        );
    }

    @ParameterizedTest
    @MethodSource("provideSessionPlatformAndClassArgs")
    @DisplayName("Input platform set builder, returns different subclass depending on platform")
    void AllSetBuilder_ReturnsOnlineSessionWithValues(String platform, Class<OnlineSession> expectedClass) {
        builder.setPlatform(platform);

        OnlineSession session = builder.build();

       assertTrue(expectedClass.isInstance(session));
    }
    static Stream<Arguments> provideSessionPlatformAndClassArgs() {
        return Stream.of(
                Arguments.of("Random Platform Name", OnlineSession.class),
                Arguments.of("Teams", TeamsOnlineSession.class),
                Arguments.of("TEAMS", TeamsOnlineSession.class),
                Arguments.of("tEAMS", TeamsOnlineSession.class)
        );
    }
}