package models;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import constants.SensorType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataRedis {

  private static final String DANGER_COLOR = "red";
  private static final String LOW_ALERT_COLOR = "yellowgreen";
  private static final String HIGH_ALERT_COLOR = "darkolivegreen";
  private static final String NORMAL_COLOR = "green";

  private Float temperature;
  private String temperatureColor;
  private Float moisture;
  private String moistureColor;
  private Float ph;
  private String phColor;
  private Float ndvi;
  private String ndviColor;
  private Float height;
  private String heightColor;
  private Boolean active;

  public SensorDataRedis(Map<String, String> sourceMap){
    System.out.println(sourceMap);

    if(sourceMap.get(SensorType.HEIGHT.name()) != null){
      this.height = Float.parseFloat(sourceMap.get(SensorType.HEIGHT.name()));
    }
    if(sourceMap.get(SensorType.TEMPRATURE.name()) != null){
      System.out.println("temperature"+sourceMap.get(SensorType.TEMPRATURE.name()));
      this.temperature = Float.parseFloat(sourceMap.get(SensorType.TEMPRATURE.name()));
    }
    if(sourceMap.get(SensorType.NDVI.name()) != null){
      this.ndvi = Float.parseFloat(sourceMap.get(SensorType.NDVI.name()));
    }
    if(sourceMap.get(SensorType.MOISTURE.name()) != null){
      this.moisture = Float.parseFloat(sourceMap.get(SensorType.MOISTURE.name()));
    }
    if(sourceMap.get(SensorType.PH.name()) != null){
      this.ph = Float.parseFloat(sourceMap.get(SensorType.PH.name()));
    }
    if(sourceMap.get(SensorType.active.name()) != null && Float.parseFloat(sourceMap.get(SensorType.active.name())) > 0.0F){
      this.active = true;
    }
  }

  public void updateColours() {
    moisture = Math.max(10, moisture);
    ndvi = (float) Math.random();
    height = (float) Math.random()*5;
    if(temperature == null)
      temperatureColor = DANGER_COLOR;
    else if(temperature < 20 )
      temperatureColor = LOW_ALERT_COLOR;
    else if(temperature > 40)
      temperatureColor = HIGH_ALERT_COLOR;
    else
      temperatureColor = NORMAL_COLOR;

    if(moisture == null)
      moistureColor = DANGER_COLOR;
    else if(moisture < 40 )
      moistureColor = LOW_ALERT_COLOR;
    else if(moisture > 60)
      moistureColor = HIGH_ALERT_COLOR;
    else
      moistureColor = NORMAL_COLOR;

    if(ph == null)
      phColor = DANGER_COLOR;
    else if(ph < 6 )
      phColor = LOW_ALERT_COLOR;
    else if(ph > 8)
      phColor = HIGH_ALERT_COLOR;
    else
      phColor = NORMAL_COLOR;

    if(ndvi == null)
      ndviColor = DANGER_COLOR;
    else if(ndvi < 0.5 )
      ndviColor = LOW_ALERT_COLOR;
    else if(ndvi > 0.8)
      ndviColor = HIGH_ALERT_COLOR;
    else
      ndviColor = NORMAL_COLOR;


    if(height == null)
      heightColor = DANGER_COLOR;
    else if(height <= 2 )
      heightColor = LOW_ALERT_COLOR;
    else if(height >= 4)
      heightColor = HIGH_ALERT_COLOR;
    else
      heightColor = NORMAL_COLOR;

    if(active == null)
      active = false;
  }
}
