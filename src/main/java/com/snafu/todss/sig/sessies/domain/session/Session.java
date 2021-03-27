package com.snafu.todss.sig.sessies.domain.session;

import javax.persistence.*;
import javax.swing.text.html.Option;
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

    @OneToMany(orphanRemoval = true)
    private List<Attendance> attendanceList;

    @ManyToOne
    private SpecialInterestGroup sig;

    @OneToMany(orphanRemoval = true)
    private List<Feedback> feedbackList;

    public Session() {
    }

    public Session(
            SessionDetails details, SessionState state
            , SpecialInterestGroup sig
            , List<Attendance> attendanceList
            , List<Feedback> feedbackList
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
        this.attendanceList.stream()
                .removeIf(attendance -> !attendance.getPerson().equals(person));
        return false;
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
