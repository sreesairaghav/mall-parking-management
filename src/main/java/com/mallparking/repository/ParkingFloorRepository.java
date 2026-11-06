package com.mallparking.repository;
import com.mallparking.entity.ParkingFloor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {
    Optional<ParkingFloor> findByFloorCode(String floorCode);
    boolean existsByFloorCode(String floorCode);
    List<ParkingFloor> findByActiveTrue();
}
