package com.snafu.todss.sig.sessies.domain.session;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.DateTimeException;
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

    @Column(name = "location")
    private String location;

    @Column(name = "online")
    private boolean isOnline;

    public SessionDetails() { }
    public SessionDetails(LocalDateTime startDate, LocalDateTime endDate, String subject, String description, String location, boolean isOnline) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
        this.description = description;
        this.location = location;
        this.isOnline = isOnline;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        if (this.startDate == null) throw new IllegalArgumentException("Start date cannot be null");
        if (this.endDate != null && startDate.isAfter(this.endDate)) throw new DateTimeException("Start date cannot be after end date");
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        if (this.endDate == null) throw new IllegalArgumentException("End date cannot be null");
        if (this.startDate != null && endDate.isAfter(this.startDate)) throw new DateTimeException("End date cannot be before end date");
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
