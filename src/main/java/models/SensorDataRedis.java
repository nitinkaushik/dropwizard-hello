package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SensorDataRedis {

  private static final String DANGER_COLOR = "red";
  private static final String LOW_ALERT_COLOR = "yellow";
  private static final String HIGH_ALERT_COLOR = "red";
  private static final String NORMAL_COLOR = "green";
  private Float temparature;
  private String temparatureColor;
  private Float moisture;
  private String moistureColor;
  private Float ph;
  private String phColor;
  private Float ndvi;
  private String ndviColor;
  private Float height;
  private String heightColor;
  private Boolean action;

  public void updateColours() {
    if(temparature == null)
      temparatureColor = DANGER_COLOR;
    else if(temparature < 20 )
      temparatureColor = LOW_ALERT_COLOR;
    else if(temparature > 40)
      temparatureColor = HIGH_ALERT_COLOR;
    else
      temparatureColor = NORMAL_COLOR;

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

    if(action == null)
      action = false;
  }
}
