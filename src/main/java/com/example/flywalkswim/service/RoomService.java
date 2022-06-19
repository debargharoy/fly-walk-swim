package com.example.flywalkswim.service;

import com.example.flywalkswim.entity.Player;
import com.example.flywalkswim.entity.Room;
import com.example.flywalkswim.repository.PlayerRepository;
import com.example.flywalkswim.repository.RoomRepository;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

  private final RoomRepository roomRepository;
  private final PlayerRepository playerRepository;
  private final ConcurrentMap<String, UUID> roomSet = new ConcurrentHashMap<>();

  private RoomService(@NonNull final RoomRepository roomRepository,
      @NonNull final PlayerRepository playerRepository) {
    this.roomRepository = roomRepository;
    this.playerRepository = playerRepository;
  }

  public Room createRoom() {
    String roomName;
    while (true) {
      roomName = RandomStringUtils.randomAlphabetic(6).toUpperCase();
      if (!roomSet.containsKey(roomName)) {
        final Room room = Room.builder().name(roomName).isActive(true).build();
        final Room savedRoom = roomRepository.save(room);
        roomSet.put(savedRoom.getName(), savedRoom.getId());
        return savedRoom;
      }
    }
  }

  public Player joinRoom(final String roomName, final String nickName) {
    if (StringUtils.isEmpty(roomName) || !roomSet.containsKey(roomName)) {
      throw new IllegalArgumentException("Invalid room name");
    }
    final Player player = Player.builder().roomId(roomSet.get(roomName)).nickName(nickName).build();
    return playerRepository.save(player);
  }

  public void deleteRoom(String roomId) {
    if (StringUtils.isEmpty(roomId) || !roomSet.containsValue(UUID.fromString(roomId))) {
      throw new IllegalArgumentException("Invalid room name");
    }
    final UUID id = UUID.fromString(roomId);
    roomRepository.deleteById(id);
    String roomName = "";
    for (Entry<String, UUID> stringUUIDEntry : roomSet.entrySet()) {
      if (stringUUIDEntry.getValue().equals(id)) {
        roomName = stringUUIDEntry.getKey();
        break;
      }
    }
    roomSet.remove(roomName);
    playerRepository.deleteByRoomId(id);
  }
}
