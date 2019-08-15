package components;

import soSafeSystem.SoSafe;

import java.util.List;

/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public class SecuritySensor extends Sensor {
    private int sSensorID;
    private int atArea;

    public SecuritySensor(int areaID){
        //count the security sensor when install a new one
        ++sSensorCount;
        //set sensor ID
        this.sSensorID =sSensorCount;
        //set sensor type
        this.sensorType = "Security";
        //set areaID to sensor
        this.atArea = areaID;
    }

    //ring the alarm if this security sensor detected a situation
    public void raiseAlarm(List<SoSafe.Alarm> alarmList, List<SoSafe.Alarm> alarmRecordList, SoSafe soSafe){
        //if security sensor soSafeSystem is on or if user set sensor working schedule
        if (soSafe.allSecuritySensorOn || soSafe.timeControlPattern) {
            //create an instance of alarm,pass this sensor, and sensor info to the alarm
            alarm = new SoSafe.Alarm(this, sSensorID, atArea);
            // add alarm into alarmList for gui
            alarmList.add(alarm);
            // add alarm into alarm record List to save alarm records and charge
            alarmRecordList.add(alarm);
        } else {
            return;
        }
    }
    public int getAtArea() {
        return atArea;
    }
    public int getSecuritySensorId() { return  this.sSensorID; }
}