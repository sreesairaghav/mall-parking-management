package com.mallparking.dto;
import com.mallparking.entity.ParkingSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class ParkingSessionResponse {
    private Long sessionId; private String registrationNumber,driverName,floorCode,slotNumber,status;
    private LocalDateTime entryTime,exitTime; private Integer durationMinutes; private BigDecimal fee;
    public ParkingSessionResponse(ParkingSession s){
        sessionId=s.getId();registrationNumber=s.getVehicle().getRegistrationNumber();driverName=s.getVehicle().getDriverName();
        floorCode=s.getParkingSlot().getParkingFloor().getFloorCode();slotNumber=s.getParkingSlot().getSlotNumber();
        entryTime=s.getEntryTime();exitTime=s.getExitTime();durationMinutes=s.getDurationMinutes();
        fee=s.getCalculatedFee();status=s.getSessionStatus().name();
    }
    public Long getSessionId(){return sessionId;} public String getRegistrationNumber(){return registrationNumber;}
    public String getDriverName(){return driverName;} public String getFloorCode(){return floorCode;}
    public String getSlotNumber(){return slotNumber;} public LocalDateTime getEntryTime(){return entryTime;}
    public LocalDateTime getExitTime(){return exitTime;} public Integer getDurationMinutes(){return durationMinutes;}
    public BigDecimal getFee(){return fee;} public String getStatus(){return status;}
}
