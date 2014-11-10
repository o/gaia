package org.ungur.gaia.dao;

public class Event {

    private Long timestamp;

    private Long count;

    public Event(Long timestamp, Long count) {
        this.timestamp = timestamp;
        this.count = count;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}


