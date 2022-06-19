package com.example.flywalkswim.model;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("data")
@Getter
@Setter
public class Organisms {
  private Map<String, List<String>> organisms;
}
