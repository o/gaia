package org.ungur.gaia.dao;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

public class PushEvent {

    private static final String EVENT_NAME_REGEX = "[a-zA-Z0-9_-]*";

    @NotEmpty
    @Pattern(regexp = EVENT_NAME_REGEX)
    private String name;

    @Min(0)
    private Long increment = 1L;

    @Min(0)
    private Long timestamp = new DateTime().getMillis() / 1000;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIncrement() {
        return increment;
    }

    public void setIncrement(Long increment) {
        this.increment = increment;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
