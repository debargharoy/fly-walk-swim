package com.example.flywalkswim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("data")
@Getter
@Setter
public class Organisms {

  private final Map<String, List<String>> organisms;
  private final List<String> organismNames;

  public Organisms(Map<String, List<String>> organisms) {
    this.organisms = organisms;
    organismNames = new ArrayList<>(organisms.keySet());
  }

  public Map<String, List<String>> randomize(final int numberOfElements) {
    Collections.shuffle(organismNames);
    Map<String, List<String>> data = new HashMap<>();
    for (int count = 0; count < numberOfElements; ++count) {
      final String key = organismNames.get(count);
      data.put(key, organisms.get(key));
    }
    return data;
  }
}
