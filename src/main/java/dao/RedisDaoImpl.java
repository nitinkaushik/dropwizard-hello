package dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import constants.EnumConstants;
import constants.SensorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import redis.clients.jedis.Jedis;

@Data
public class RedisDaoImpl {

  Jedis jedis;
  ObjectMapper objectMapper;

  public RedisDaoImpl(){
    jedis = new Jedis("localhost");
    jedis.set("foo", "bar");
    String value = jedis.get("foo");
    System.out.println(value);

  }

  public void insert(Integer sensorId, SensorType sensorType, Float sensorData){
    System.out.println("inserting into "+sensorId + " "+sensorType+ " "+sensorData);
    jedis.hset(sensorId.toString(), sensorType.toString(), sensorData.toString());
  }

  public Map getMap(String key){
    return jedis.hgetAll(key);
  }

  public List<Map> getMaps(){
    List<Map> maps = new ArrayList<>();
    for(Integer i=1;i<=16;i++){
      maps.add(jedis.hgetAll(i.toString()));
    }
    return maps;
  }
}
