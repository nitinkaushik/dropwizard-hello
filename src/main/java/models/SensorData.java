package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorData {
    @JsonProperty
    private float temparature;
    @JsonProperty
    private float moisture;
    @JsonProperty
    private float ph;

    @JsonCreator
    public SensorData(@JsonProperty("temparature") float temparature, @JsonProperty("moisture") float moisture, @JsonProperty("ph") float ph) {
        this.temparature = temparature;
        this.moisture = moisture;
        this.ph = ph;
    }

    public float getTemparature() {
        return temparature;
    }

    public float getMoisture() {
        return moisture;
    }

    public float getPh() {
        return ph;
    }
}
