package com.snafu.todss.sig.sessies.domain.session;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.Duration;
import java.time.LocalDateTime;


@Embeddable
public class SessionDetails {
    private static final int MAXIMUM_SESSION_LENGTH_IN_MS = 604800000;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "subject")
    private String subject;

    @Column(name = "description")
    private String description;

    public SessionDetails() { }
    public SessionDetails(LocalDateTime startDate, LocalDateTime endDate, String subject, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        if (startDate == null) throw new IllegalArgumentException("Start date cannot be null");
        checkForSessionStartBeforeEnd(startDate, this.endDate);
        checkForSessionDuration(startDate, this.endDate);
        this.startDate = startDate;
    }

    private void checkForSessionStartBeforeEnd(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)
        ) {
            throw new IllegalArgumentException("Start date must come before the end date");
        }
    }

    private void checkForSessionDuration(LocalDateTime startDate, LocalDateTime endDate) {
       if ( endDate != null &&
                startDate != null &&
                Math.abs(Duration.between(endDate, startDate).toMillis()) > MAXIMUM_SESSION_LENGTH_IN_MS
        ) {
            throw new IllegalArgumentException(String.format("Session duration cannot be longer than %s milliseconds", MAXIMUM_SESSION_LENGTH_IN_MS));
        }
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        if (endDate == null) throw new IllegalArgumentException("End date cannot be null");
        checkForSessionStartBeforeEnd(this.startDate, endDate);
        checkForSessionDuration(this.startDate, endDate);
        this.endDate = endDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
