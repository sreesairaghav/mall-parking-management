package com.mallparking.dto;
public class DashboardStats {
    private int totalSlots, occupiedSlots, availableSlots, carsParkedToday;
    private double occupancyPercentage, todayRevenue;
    public DashboardStats() {}
    public DashboardStats(int totalSlots, int occupiedSlots) {
        this.totalSlots=totalSlots; this.occupiedSlots=occupiedSlots;
        this.availableSlots=totalSlots-occupiedSlots;
        this.occupancyPercentage=totalSlots>0?Math.round(occupiedSlots*1000.0/totalSlots)/10.0:0.0;
    }
    public int getTotalSlots(){return totalSlots;} public int getOccupiedSlots(){return occupiedSlots;}
    public int getAvailableSlots(){return availableSlots;} public double getOccupancyPercentage(){return occupancyPercentage;}
    public double getTodayRevenue(){return todayRevenue;} public int getCarsParkedToday(){return carsParkedToday;}
    public void setTotalSlots(int v){totalSlots=v;} public void setOccupiedSlots(int v){occupiedSlots=v;}
    public void setAvailableSlots(int v){availableSlots=v;} public void setOccupancyPercentage(double v){occupancyPercentage=v;}
    public void setTodayRevenue(double v){todayRevenue=v;} public void setCarsParkedToday(int v){carsParkedToday=v;}
}
