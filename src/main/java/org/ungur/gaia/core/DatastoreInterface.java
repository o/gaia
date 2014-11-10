package org.ungur.gaia.core;

import org.ungur.gaia.dao.Event;

import java.util.List;

public interface DatastoreInterface {

    public boolean push(String event, Long increment, Long timestamp);

    public List<Event> query(String event, Long start, Long end, String resolution);
}
