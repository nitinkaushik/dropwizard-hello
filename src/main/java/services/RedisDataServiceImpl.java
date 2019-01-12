package services;

import constants.EnumConstants;
import constants.SensorType;
import dao.RedisDaoImpl;
import java.util.Map;
import javax.inject.Inject;

public class RedisDataServiceImpl implements RedisDataService {

  RedisDaoImpl redisDaoImpl;

  @Inject
  public RedisDataServiceImpl(RedisDaoImpl redisDaoImpl){
    this.redisDaoImpl = redisDaoImpl;
  }

  @Override
  public void dumpData(int sensorId, Map<SensorType, Float> sensorData) {
    redisDaoImpl.insert(sensorData);
  }

  public Map getData(){
    return redisDaoImpl.getMap(EnumConstants.mapKey);
  }
}
