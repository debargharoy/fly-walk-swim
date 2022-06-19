package com.example.flywalkswim.cache;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class ScoreCache {

  private static final ConcurrentMap<UUID, Integer> scores = new ConcurrentHashMap<>();

  public void addToCache(final UUID uuid) {
    scores.put(uuid, 0);
  }

  public void removeFromCache(final List<UUID> uuids) {
    uuids.forEach(scores::remove);
  }

  public boolean updateScore(final UUID uuid, int increment) {
    final Integer currentValue = scores.get(uuid);
    scores.replace(uuid, currentValue + increment);
    return true;
  }

}
