package com.snafu.todss.sig.sessies.domain.session;

import javax.persistence.*;
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

    public Session() { }
    public Session(
            SessionDetails details, SessionState state
            ,SpecialInterestGroup sig
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
          //check if person already in session
        Attendance attendance = new Attendance();
        this.attendanceList.add(attendance);
        return false;
    }

    public boolean removeAttendee(Person person) {
         return false;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public UUID getId() {
        return id;
    }
}
