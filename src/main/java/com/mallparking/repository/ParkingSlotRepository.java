package com.mallparking.repository;
import com.mallparking.entity.ParkingSlot;
import com.mallparking.entity.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByParkingFloorId(Long floorId);
    List<ParkingSlot> findByStatus(SlotStatus status);
    List<ParkingSlot> findByParkingFloorIdAndStatus(Long floorId, SlotStatus status);
    Optional<ParkingSlot> findByParkingFloorIdAndSlotNumber(Long floorId, String slotNumber);
    long countByParkingFloorId(Long floorId);
    long countByParkingFloorIdAndStatus(Long floorId, SlotStatus status);
    long countByStatus(SlotStatus status);
}
