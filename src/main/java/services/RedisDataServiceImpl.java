package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import constants.EnumConstants;
import constants.SensorType;
import dao.RedisDaoImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import models.SensorData;
import models.SensorDataRedis;
import org.apache.commons.lang3.tuple.Pair;
import sensorRepo.SensorRepo;

public class RedisDataServiceImpl implements RedisDataService {

  RedisDaoImpl redisDaoImpl;
  SensorRepo sensorRepo;
  ObjectMapper objectMapper;

  @Inject
  public RedisDataServiceImpl(RedisDaoImpl redisDaoImpl, SensorRepo sensorRepo){
    this.sensorRepo = sensorRepo;
    this.redisDaoImpl = redisDaoImpl;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void dumpData(int sensorId, SensorType sensorType, Float sensorData) {
    redisDaoImpl.insert(sensorId, sensorType, sensorData);
  }

  public List<SensorDataRedis> getData(){
    List<Map> maps = redisDaoImpl.getMaps();
    List<SensorDataRedis> sensorDataRedisList = new ArrayList<>();
    for(int i=0;i<maps.size();i++){
      SensorDataRedis sensorDataRedis;
      try {
        System.out.println("ith map data"+maps.get(i));
        sensorDataRedis = new SensorDataRedis(maps.get(i));
      }catch (Exception e){
        sensorDataRedis = new SensorDataRedis();
      }
      sensorDataRedis.updateColours();
      sensorDataRedisList.add(sensorDataRedis);
    }

    return sensorDataRedisList;
  }
}
