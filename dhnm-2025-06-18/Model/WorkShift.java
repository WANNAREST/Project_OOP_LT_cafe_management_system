package Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WorkShift {
    private final StringProperty day;
    private final StringProperty morning;
    private final StringProperty afternoon;
    private final StringProperty evening;
    
    public WorkShift(String day, String morning, String afternoon, String evening) {
        this.day = new SimpleStringProperty(day);
        this.morning = new SimpleStringProperty(morning);
        this.afternoon = new SimpleStringProperty(afternoon);
        this.evening = new SimpleStringProperty(evening);
    }

    // Getter methods
    public String getDay() { return day.get(); }
    public String getMorning() { return morning.get(); }
    public String getAfternoon() { return afternoon.get(); }
    public String getEvening() { return evening.get(); }

    // Property methods
    public StringProperty dayProperty() { return day; }
    public StringProperty morningProperty() { return morning; }
    public StringProperty afternoonProperty() { return afternoon; }
    public StringProperty eveningProperty() { return evening; }
}