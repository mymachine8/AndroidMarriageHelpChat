package com.happyhome.kkommanapall.model;

/**
 * Created by kkommanapall on 10/19/2015.
 */
public class CareService {
    private int CareServiceId;
    private String ServiceName;

    public int getCareServiceId() {
        return CareServiceId;
    }

    public void setCareServiceId(int CareServiceId) {
        this.CareServiceId = CareServiceId;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String ServiceName) {
        ServiceName = ServiceName;
    }

    @Override
    public String toString(){
        return ServiceName;
    }

}
