package system;

import java.util.List;

/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public abstract class Sensor {
    protected int atArea;
    protected static int sSensorCount = 0,fSensorCount=0;
    protected boolean status;
    protected String sensorType;
    protected SoSafe.Alarm alarm;

    public Sensor(){}
    public Sensor(int areaID) {
        //pass area ID to save as areaID in sensor
        atArea = areaID;
        //sensor working status is off
        status = false;
    }
    // turn on this sensor
    public void turnOnSensor(){
        this.status = true;
    }
    //turn off this sensor
    public void turnOffSensor(){this.status = false;}
    //sensor could raise alarm
    public abstract void raiseAlarm(List<SoSafe.Alarm> alarmList, List<SoSafe.Alarm> alarmRecordList,SoSafe soSafe);


}
