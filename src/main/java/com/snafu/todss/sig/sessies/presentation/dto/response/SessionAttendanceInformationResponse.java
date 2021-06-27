package com.snafu.todss.sig.sessies.presentation.dto.response;

public class SessionAttendanceInformationResponse {
    private int reactionAmount;
    private int attendeeAmount;
    private int absentAmount;

    public SessionAttendanceInformationResponse(int reactionAmount, int attendeeAmount, int absentAmount) {
        this.reactionAmount = reactionAmount;
        this.attendeeAmount = attendeeAmount;
        this.absentAmount = absentAmount;
    }

    public int getReactionAmount() {
        return reactionAmount;
    }

    public void setReactionAmount(int reactionAmount) {
        this.reactionAmount = reactionAmount;
    }

    public int getAttendeeAmount() {
        return attendeeAmount;
    }

    public void setAttendeeAmount(int attendeeAmount) {
        this.attendeeAmount = attendeeAmount;
    }

    public int getAbsentAmount() {
        return absentAmount;
    }

    public void setAbsentAmount(int absentAmount) {
        this.absentAmount = absentAmount;
    }
}
