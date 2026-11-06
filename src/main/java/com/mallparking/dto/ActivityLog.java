package com.mallparking.dto;
public class ActivityLog {
    public enum EventType { ENTRY, EXIT }
    private String vehicleNumber, floor, slot, time, driverName;
    private EventType eventType;
    public ActivityLog() {}
    public ActivityLog(String vehicleNumber,String floor,String slot,String time,EventType eventType,String driverName){
        this.vehicleNumber=vehicleNumber;this.floor=floor;this.slot=slot;this.time=time;this.eventType=eventType;this.driverName=driverName;
    }
    public boolean isEntry(){return eventType==EventType.ENTRY;}
    public String getVehicleNumber(){return vehicleNumber;} public String getFloor(){return floor;}
    public String getSlot(){return slot;} public String getTime(){return time;}
    public EventType getEventType(){return eventType;} public String getDriverName(){return driverName;}
    public void setVehicleNumber(String v){vehicleNumber=v;} public void setFloor(String v){floor=v;}
    public void setSlot(String v){slot=v;} public void setTime(String v){time=v;}
    public void setEventType(EventType v){eventType=v;} public void setDriverName(String v){driverName=v;}
}
