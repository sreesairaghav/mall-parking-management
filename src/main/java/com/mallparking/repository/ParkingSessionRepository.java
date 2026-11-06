package com.mallparking.repository;
import com.mallparking.entity.ParkingSession;
import com.mallparking.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
    List<ParkingSession> findBySessionStatus(SessionStatus sessionStatus);
    Optional<ParkingSession> findFirstByVehicleIdAndSessionStatusOrderByEntryTimeDesc(Long vehicleId, SessionStatus sessionStatus);
    Optional<ParkingSession> findFirstByParkingSlotIdAndSessionStatus(Long slotId, SessionStatus sessionStatus);
    long countBySessionStatus(SessionStatus sessionStatus);
    List<ParkingSession> findByEntryTimeBetween(LocalDateTime start, LocalDateTime end);
}
