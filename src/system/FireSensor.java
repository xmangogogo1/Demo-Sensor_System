package system;

import java.util.List;

/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public class FireSensor extends Sensor {

    private int fSensorID;
    private int atArea;

    public FireSensor(int areaID){
        // count the fire sensor when install a fire sensor
        ++fSensorCount;
        //set fire sensor ID
        this.fSensorID = fSensorCount;
        //set sensor type to fire
        this.sensorType = "Fire";
        //set areaID
        this.atArea = areaID;
    }
    //ring the alarm when the sensor detected something
    public void raiseAlarm(List<SoSafe.Alarm> alarmList, List<SoSafe.Alarm> alarmRecordList, SoSafe soSafe){
        //if fire sensor system is on or if user set sensor working schedule
        if (soSafe.allFireSensorOn || soSafe.timeControlPattern == true) {
            //create an instance of alarm,pass this sensor, and sensor info to the alarm
            alarm = new SoSafe.Alarm(this, fSensorID, atArea);
            // add alarm into alarmList for gui
            alarmList.add(alarm);
            // add alarm into alarm record List to save alarm records and charge
            alarmRecordList.add(alarm);

        } else {// do nothing if system is not turn on or sensor is not scheduled
            return;
        }
    }
    public int getAtArea() {return atArea;}
    public int getFireSensorId(){
        return this.fSensorID;
    }
}