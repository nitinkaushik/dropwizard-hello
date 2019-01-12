package services;

import constants.SensorType;
import java.util.Map;

public interface RedisDataService {

  /**
   * dumps an object against sensor id
   * @param sensorId
   * @param sensorData
   */
  public void dumpData(int sensorId, Map<SensorType, Float> sensorData);

}
