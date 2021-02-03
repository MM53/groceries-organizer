package org.example.valueObjects;

public class Location {

    private final String room;
    private final String place;
    private final String shelf;

    public Location(String room, String place, String shelf) {
        this.room = room;
        this.place = place;
        this.shelf = shelf;
    }

    public String getRoom() {
        return room;
    }

    public String getPlace() {
        return place;
    }

    public String getShelf() {
        return shelf;
    }
}
