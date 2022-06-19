package com.example.flywalkswim.controller;

import com.example.flywalkswim.dto.PlayerJoin;
import com.example.flywalkswim.entity.Player;
import com.example.flywalkswim.entity.Room;
import com.example.flywalkswim.service.RoomService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LandingPageController {

  private final RoomService roomService;

  public LandingPageController(final RoomService roomService) {
    this.roomService = roomService;
  }

  @PostMapping("create-room")
  public Room createRoom() {
    return roomService.createRoom();
  }

  @PostMapping("join-room")
  public Player joinRoom(@RequestBody PlayerJoin player) {
    return roomService.joinRoom(player.getRoomName(), player.getUserNickName());
  }

  @DeleteMapping("destroy-room/{roomId}")
  public void deleteRoom(@PathVariable final String roomId) {
    roomService.deleteRoom(roomId);
  }

}
