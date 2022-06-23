package com.example.flywalkswim.controller;

import com.example.flywalkswim.model.Organisms;
import com.example.flywalkswim.model.PlayerScore;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController("/{roomId}")
public class EventsController {

  private final Logger logger = LoggerFactory.getLogger(EventsController.class);
  private final Organisms organisms;
  // Room ID -> List of emitters for the room, where each emitter is a player
  private final ConcurrentMap<UUID, Set<SseEmitter>> sseEmittersByRoom = new ConcurrentHashMap<>();
  // roomId -> score for each player
  private final ConcurrentMap<UUID, List<PlayerScore>> scoreMapByRoom = new ConcurrentHashMap<>();

  public EventsController(final Organisms organisms) {
    this.organisms = organisms;
  }

  @GetMapping("/organisms")
  public SseEmitter getOrganisms(@PathVariable final String roomId) {
    final SseEmitter emitter = new SseEmitter();
    final UUID roomUuid = UUID.fromString(roomId);
    final Set<SseEmitter> currentRoomEmitters = sseEmittersByRoom.getOrDefault(
        roomUuid,
        Collections.synchronizedSet(new HashSet<>()));
    currentRoomEmitters.add(emitter);
    if (sseEmittersByRoom.containsKey(roomUuid)) {
      sseEmittersByRoom.replace(roomUuid, currentRoomEmitters);
    } else {
      sseEmittersByRoom.put(roomUuid, currentRoomEmitters);
    }
    emitter.onCompletion(() -> {
      logger.debug("Attempting removal for emitter={}", emitter);
      final Set<SseEmitter> sseEmitters = sseEmittersByRoom.get(roomUuid);
      sseEmitters.remove(emitter);
      sseEmittersByRoom.replace(roomUuid, sseEmitters);
      logger.info("Removed emitter={} from room={}", emitter, roomUuid);
    });
    emitter.onError(
        throwable -> logger.error(String.format("Error on emitter=%s", emitter), throwable));
    emitter.onTimeout(() -> logger.info("Timeout for emitter={}", emitter));
    return emitter;
  }

  @GetMapping("/play")
  public void play(@PathVariable final String roomId, @RequestParam final Integer rounds) {
    final UUID roomUuid = UUID.fromString(roomId);
    final Set<SseEmitter> players = sseEmittersByRoom.get(roomUuid);
    players.forEach(player -> {
      try {
        final Map<String, List<String>> listOfOrganisms = organisms.randomize(
            rounds == null ? 60 : rounds);
        player.send(SseEmitter.event().name(roomId).data(listOfOrganisms).build());
      } catch (IOException e) {
        logger.error(String.format("Failed to emit data to emitter=%s", player), e);
      }
    });
  }

  @PostMapping("/score/update")
  public void updateScore(@PathVariable String roomId, @RequestBody PlayerScore playerScore) {
    final UUID uuid = UUID.fromString(roomId);
    if (scoreMapByRoom.containsKey(uuid)) {
      final List<PlayerScore> currentList = scoreMapByRoom.get(uuid);
      currentList.add(playerScore);
      scoreMapByRoom.replace(uuid, currentList);
    } else {
      scoreMapByRoom.put(uuid, Collections.singletonList(playerScore));
    }
  }

  @GetMapping("/score/view")
  public List<PlayerScore> viewScore(@PathVariable String roomId) {
    final UUID uuid = UUID.fromString(roomId);
    return scoreMapByRoom.getOrDefault(uuid, Collections.emptyList());
  }

  public void delete(final UUID roomId) {
    if (sseEmittersByRoom.containsKey(roomId)) {
      sseEmittersByRoom.get(roomId).forEach(ResponseBodyEmitter::complete);
      sseEmittersByRoom.remove(roomId);
    }
  }

}
