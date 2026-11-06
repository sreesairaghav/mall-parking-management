package com.mallparking.dto;
public class FloorStats {
    private String floorName, floorCode, statusBadge;
    private int totalSlots, occupiedSlots, availableSlots;
    private double occupancyPercentage;
    public FloorStats() {}
    public FloorStats(String floorName,String floorCode,int totalSlots,int occupiedSlots){
        this(floorName,floorCode,totalSlots,occupiedSlots,totalSlots-occupiedSlots);
    }
    public FloorStats(String floorName,String floorCode,int totalSlots,int occupiedSlots,int availableSlots){
        this.floorName=floorName;this.floorCode=floorCode;this.totalSlots=totalSlots;
        this.occupiedSlots=occupiedSlots;this.availableSlots=availableSlots;
        this.occupancyPercentage=totalSlots>0?Math.round(occupiedSlots*1000.0/totalSlots)/10.0:0.0;
        this.statusBadge=occupancyPercentage>=95?"FULL":occupancyPercentage>=70?"HIGH":occupancyPercentage>=40?"MODERATE":"LOW";
    }
    public String getFloorName(){return floorName;} public String getFloorCode(){return floorCode;}
    public int getTotalSlots(){return totalSlots;} public int getOccupiedSlots(){return occupiedSlots;}
    public int getAvailableSlots(){return availableSlots;} public double getOccupancyPercentage(){return occupancyPercentage;}
    public String getStatusBadge(){return statusBadge;}
    public void setFloorName(String v){floorName=v;} public void setFloorCode(String v){floorCode=v;}
    public void setTotalSlots(int v){totalSlots=v;} public void setOccupiedSlots(int v){occupiedSlots=v;}
    public void setAvailableSlots(int v){availableSlots=v;} public void setOccupancyPercentage(double v){occupancyPercentage=v;}
    public void setStatusBadge(String v){statusBadge=v;}
}
