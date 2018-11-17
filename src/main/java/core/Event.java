package core;

public class Event {
    // here can be setters/getters but to evade boilerplate keeping it simple
    public String type;
    public String data;

    public Event(String type, String data) {
        this.type = type;
        this.data = data;
    }
}
