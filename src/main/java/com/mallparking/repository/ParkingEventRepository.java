package com.mallparking.repository;
import com.mallparking.entity.ParkingEvent;
import com.mallparking.entity.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface ParkingEventRepository extends JpaRepository<ParkingEvent, Long> {
    List<ParkingEvent> findByParkingSessionId(Long sessionId);
    List<ParkingEvent> findByEventType(EventType eventType);
    List<ParkingEvent> findByEventTimeBetweenOrderByEventTimeDesc(LocalDateTime start, LocalDateTime end);
}
