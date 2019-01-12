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

    public void setTemparature(float temparature) {
        this.temparature = temparature;
    }

    public void setMoisture(float moisture) {
        this.moisture = moisture;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }
}
