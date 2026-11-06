package com.mallparking.dto;
import com.mallparking.entity.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class EntryRequest {
    @NotBlank @Size(max=20) private String registrationNumber;
    @NotBlank @Size(max=100) private String driverName;
    @Size(max=15) private String phoneNumber;
    @NotNull private VehicleType vehicleType;
    private String floorCode;
    public String getRegistrationNumber(){return registrationNumber;} public void setRegistrationNumber(String v){registrationNumber=v;}
    public String getDriverName(){return driverName;} public void setDriverName(String v){driverName=v;}
    public String getPhoneNumber(){return phoneNumber;} public void setPhoneNumber(String v){phoneNumber=v;}
    public VehicleType getVehicleType(){return vehicleType;} public void setVehicleType(VehicleType v){vehicleType=v;}
    public String getFloorCode(){return floorCode;} public void setFloorCode(String v){floorCode=v;}
}
