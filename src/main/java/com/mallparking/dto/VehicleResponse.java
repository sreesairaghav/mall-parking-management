package com.mallparking.dto;
import com.mallparking.entity.Vehicle;
import com.mallparking.entity.enums.VehicleType;
public class VehicleResponse {
    private Long id; private String registrationNumber,driverName,phoneNumber; private VehicleType vehicleType;
    public VehicleResponse(Vehicle v){id=v.getId();registrationNumber=v.getRegistrationNumber();driverName=v.getDriverName();phoneNumber=v.getPhoneNumber();vehicleType=v.getVehicleType();}
    public Long getId(){return id;} public String getRegistrationNumber(){return registrationNumber;}
    public String getDriverName(){return driverName;} public String getPhoneNumber(){return phoneNumber;}
    public VehicleType getVehicleType(){return vehicleType;}
}
