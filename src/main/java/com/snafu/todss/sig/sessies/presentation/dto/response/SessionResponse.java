package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {
    private UUID id;
    private SessionState state;
    private SessionDetails details;
    private String type;
    private SpecialInterestGroupResponse specialInterestGroup;
    private String address;
    private String platform;
    private String joinUrl;

    public SessionResponse() {
        //For Modelmapper to map domain class to this DTO
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public SessionState getState() {
        return state;
    }

    public SessionDetails getDetails() {
        return details;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public void setDetails(SessionDetails details) {
        this.details = details;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public SpecialInterestGroupResponse getSpecialInterestGroup() {
        return specialInterestGroup;
    }

    public void setSpecialInterestGroup(SpecialInterestGroupResponse specialInterestGroup) {
        this.specialInterestGroup = specialInterestGroup;
    }
}
