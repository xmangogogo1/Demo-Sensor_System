package soSafeSystem;

/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public class Area {
    private int AreaId;
    private int startHour;
    private int endHour ;
    private int startMin;
    private int endMin;
    boolean fireSensorInstalled;
    boolean securitySensorInstalled;
    private boolean sSensorsOn, fSensorsOn;
    private boolean  areaSelectedStatus;

    public Area(int Id) {
        // area sensor soSafeSystem startHour default = 0;
        startHour = 0;
        endHour = 0;
        //set area ID start from 0
        this.AreaId = Id;

        // area is selected ready to install any sensor soSafeSystem
        areaSelectedStatus = true;
        //false if this this area has not been installed any fire sensor
        fireSensorInstalled = false;
        //false if this this area has not been installed any security sensor
        securitySensorInstalled = false;
        //false if this area's fire sensor is not working
        fSensorsOn =true;
        //false if this area's security sensor is not wokring
        sSensorsOn =true;
    }

    //get sensor start working hour
    public int getStartHour() {
        return startHour;
    }
    //set sensor start working hour
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    //get sensor end working hour
    public int getEndHour() {
        return endHour;
    }
    //set sensor end working hour
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    //Check if this area has been select to install soSafeSystem
    public int getStartMin() {return startMin;}
    public void setStartMin(int startMin) {this.startMin = startMin;}
    public int getEndMin() {return endMin;}
    public void setEndMin(int endMin) {this.endMin = endMin;}

    //true if this this area installed fire sensor
    public void setFireSensorInstalled(){fireSensorInstalled =true;}
    //true if this this area installed security sensor
    public void setSecuritySensorInstalled(){securitySensorInstalled =true;}
    //get aread ID
    public int getAreaId(){return AreaId;}

}
