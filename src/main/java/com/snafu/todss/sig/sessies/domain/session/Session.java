package com.snafu.todss.sig.sessies.domain.session;

import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    private SessionDetails details;

    @Enumerated(EnumType.STRING)
    private SessionState state;

    @ManyToOne
    private SpecialInterestGroup sig;

    @OneToMany(orphanRemoval = true)
    private List<Attendance> attendanceList;

    @OneToMany(orphanRemoval = true)
    private List<Feedback> feedbackList;

    public Session() {
    }

    public Session(
            SessionDetails details,
            SessionState state,
            SpecialInterestGroup sig,
            List<Attendance> attendanceList,
            List<Feedback> feedbackList
    ) {
        this.details = details;
        this.state = state;
        this.sig = sig;
        this.attendanceList = attendanceList;
        this.feedbackList = feedbackList;
    }

    public SessionDetails getDetails() {
        return details;
    }

    public List<Attendance> getAttendances() {
        return attendanceList;
    }

    public boolean addAttendee(Person person) {
        boolean noneMatch = Optional.of(
                this.attendanceList.stream()
                        .map(Attendance::getPerson)
                        .noneMatch(attendancePerson -> attendancePerson.equals(person))
        ).orElseThrow(() -> new IllegalArgumentException("Session already has person"));
        //of zonder optional en throws
        if (noneMatch) {
            Attendance attendance = new Attendance();
            return this.attendanceList.add(attendance);
        }
       return false;
    }

    public boolean removeAttendee(Person person) {
        return this.attendanceList.removeIf(attendance -> !attendance.getPerson().equals(person));
    }

    public List<Feedback> getFeedback() {
        return feedbackList;
    }

    public boolean addFeedback(Feedback feedback) {
        if (this.feedbackList.contains(feedback)){
            return false;
        }
        return this.feedbackList.add(feedback);
    }

    public boolean removeFeedback(Feedback feedback) {
        return this.feedbackList.remove(feedback);
    }

    public SpecialInterestGroup getSig() {
        return sig;
    }

    public SessionState getState() {
        return state;
    }

    public void nextState() {
        this.state = state.next();
    }

    public UUID getId() {
        return id;
    }
}
