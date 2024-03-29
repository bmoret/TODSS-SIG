package com.snafu.todss.sig.sessies.domain.session.types;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionListener;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.snafu.todss.sig.sessies.util.InputValidations.inputNotNull;

@EntityListeners(SessionListener.class)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Session {
    @Id
    @GeneratedValue(generator="generator")
    @GenericGenerator(
            name="generator",
            strategy="com.snafu.todss.sig.sessies.domain.idgenerator.FilterIdentifierGenerator"
    )
    @Column(unique=true, nullable=false)
    private UUID id;

    @Embedded
    private SessionDetails details;

    @Enumerated(EnumType.STRING)
    private SessionState state;

    @ManyToOne
    private SpecialInterestGroup sig;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Attendance> attendanceList;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Feedback> feedbackList;

    @OneToOne
    private Person contactPerson;

    protected Session() {
    }

    protected Session(
            SessionDetails details,
            SessionState state,
            SpecialInterestGroup sig,
            List<Attendance> attendanceList,
            List<Feedback> feedbackList,
            Person contactPerson
    ) {
        this.details = details;
        this.state = state;
        this.sig = sig;
        this.attendanceList = attendanceList;
        this.feedbackList = feedbackList;
        this.contactPerson = contactPerson;
    }

    public SessionDetails getDetails() {
        return details;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void addAllAttendees(List<Attendance> attendances) {
        attendances.forEach(this::addAttendee);
    }

    public void addAllFeedback(List<Feedback> feedbacks) {
        feedbacks.forEach(this::addFeedback);
    }

    public List<Attendance> getAttendances() {
        return List.copyOf(attendanceList);
    }

    public boolean addAttendee(Attendance attendance) {
        inputNotNull(attendance);
        boolean isPersonAttendingSession = this.attendanceList.stream()
                .map(Attendance::getPerson)
                .anyMatch(attendancePerson -> attendancePerson.equals(attendance.getPerson()));
        if (isPersonAttendingSession) {
            return false;
        }
        return this.attendanceList.add(attendance);
    }

    public boolean removeAttendee(Person person) {
        return this.attendanceList.removeIf(attendance ->  attendance.getPerson().equals(person));
    }

    public List<Feedback> getFeedback() {
        return List.copyOf(feedbackList);
    }

    public boolean addFeedback(Feedback feedback) {
        inputNotNull(feedback);
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

    public void setSig(SpecialInterestGroup sig) {
        this.sig = sig;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) &&
                Objects.equals(details, session.details) &&
                state == session.state &&
                Objects.equals(sig, session.sig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, details, state, sig);
    }
}
