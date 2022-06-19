package com.example.flywalkswim.repository;

import com.example.flywalkswim.entity.Player;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerRepository extends CrudRepository<Player, UUID> {

  @Transactional
  int deleteByRoomId(UUID roomId);

}
