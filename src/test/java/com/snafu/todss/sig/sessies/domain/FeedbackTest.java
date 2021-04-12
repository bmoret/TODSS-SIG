package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class FeedbackTest {
    @Test
    @DisplayName("Creating feedback")
    void creatingCorrectFeedback(){
        Session session = mock(Session.class);
        Person person = mock(Person.class);
        String description = "This is an example of a description someone has filled in based off of their experience of a session";

        Feedback feedback = new Feedback(description, session, person);

        assertEquals(description, feedback.getDescription());
        assertEquals(session, feedback.getSession());
        assertEquals(person, feedback.getPerson());
    }

    @Test
    @DisplayName("Creating feedback without filling in an description")
    void creatingInCorrectFeedback(){
        Session session = mock(Session.class);
        Person person = mock(Person.class);

        assertThrows(IllegalArgumentException.class, () -> new Feedback("", session, person));
    }

}