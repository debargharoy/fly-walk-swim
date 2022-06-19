package com.example.flywalkswim.repository;

import com.example.flywalkswim.entity.Room;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room, UUID> {

}
