package com.example.flywalkswim.model;

import com.example.flywalkswim.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerScore {
  private Player player;
  private Integer score;
}
