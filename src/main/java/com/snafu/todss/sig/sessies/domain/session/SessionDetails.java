package com.snafu.todss.sig.sessies.domain.session;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;


@Embeddable
public class SessionDetails {
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
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        if (endDate == null) throw new IllegalArgumentException("End date cannot be null");
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
