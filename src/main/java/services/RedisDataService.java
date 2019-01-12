package services;

//import com.google.inject.ImplementedBy;
import constants.SensorType;
import java.util.List;
import java.util.Map;
import models.SensorDataRedis;

//@ImplementedBy(RedisDataServiceImpl.class)
public interface RedisDataService {

  /**
   * dumps an object against sensor id
   * @param sensorId
   */
  public void dumpData(int sensorId, SensorType sensorType, Float SensorData);

  public List<SensorDataRedis> getData();

}
