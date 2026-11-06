package com.mallparking.dto;
import com.mallparking.entity.ParkingSlot;
public class SlotResponse {
    private Long id; private String floorCode,floorName,slotNumber,slotType,status;
    public SlotResponse(ParkingSlot s,boolean occupied){
        id=s.getId();floorCode=s.getParkingFloor().getFloorCode();floorName=s.getParkingFloor().getFloorName();
        slotNumber=s.getSlotNumber();slotType=s.getSlotType();status=occupied?"OCCUPIED":s.getStatus().name();
    }
    public Long getId(){return id;} public String getFloorCode(){return floorCode;}
    public String getFloorName(){return floorName;} public String getSlotNumber(){return slotNumber;}
    public String getSlotType(){return slotType;} public String getStatus(){return status;}
}
