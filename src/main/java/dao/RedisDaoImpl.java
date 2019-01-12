package dao;

import constants.EnumConstants;
import java.util.Map;
import lombok.Data;
import redis.clients.jedis.Jedis;

@Data
public class RedisDaoImpl {

  Jedis jedis;

  public RedisDaoImpl(){
    jedis = new Jedis("localhost");
    jedis.set("foo", "bar");
    String value = jedis.get("foo");
    System.out.println(value);
  }

  public void insert(Map data ){
    jedis.hmset(EnumConstants.mapKey, data);
  }

  public Map getMap(String key){
    return jedis.hgetAll(key)
  }

}
