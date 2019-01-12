package sensorRepo;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Data
public class SensorRepo {

  private static Map<Integer, Pair<Integer, Integer>> staticSensorMapRepo ;

  public SensorRepo(){
    staticSensorMapRepo = new HashMap<>();
    for(int i = 0; i<16;i++)
      staticSensorMapRepo.put(i+1,new ImmutablePair<>(i/4,i%4));
  }

}
