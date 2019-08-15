package system;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public class SoSafe extends Observable{

    private List<Integer> areaIds;//List of areaIDs
    private List<Area> areaList; // list of areas

    private List<FireSensor> fSensorList;//List of fire sensors
    private List<SecuritySensor> sSensorList;//list of security sensors
    private List<FireSensor> activeFSensorList; //list of active fire sensors
    private List<SecuritySensor> activeSSensorList;//list of active security sensors

    private Map<Integer,int[]> fireSensorDistribution; //map of area Id, fire sensor IDs
    private Map<Integer,int[]> securitySensorDistribution;    //map of area Id, security sensor IDs

    static boolean allActiveFSensorOn;//status of the active fire sensors
    static boolean allActiveSSensorOn; //status of the active security sensors

    public boolean allFireSensorOn;// working status of all fire sensors
    public boolean allSecuritySensorOn;// working status of all all security sensors

    public boolean installDone;//finished install
    public boolean allControlPattern;//system get command to turn on all sensors each time
    public boolean timeControlPattern;//system get command to turn on both f and s sensors each time by room
    public boolean alarmSatus; //alarm ringing status

    private static int fireAlarmCount = 0; //fire alarm counter
    private static int securityAlarmCount = 0;// security alarm counter
    public List<Alarm> alarmList;//list of ringing alarms
    public List<Alarm> alarmRecordList;//save for records

    private char[] password;
    private LocalDateTime fsystemInitialTime; //fire sensor install time
    private LocalDateTime ssystemInitialTime; //security sensor install time
    private Date soSafeInitialTime;// soSafe system install time
    private File file;// file to save records
    private String customerInfo;// customer info of name,address,contact number
    private static String monitorPhoneNum;// monitor phone number

    private static Timer timer;
    private static List<Timer> timers; // a list of all ringing alarms that wait to call monitor
    public boolean editable = false;//state of the layoutPanel if true,it is editable

    public SoSafe(){
        fSensorList= new ArrayList<>();
        sSensorList =new ArrayList<>();
        fireSensorDistribution =new HashMap<>();
        securitySensorDistribution =new HashMap<>();
        areaList =new ArrayList<>();
        areaIds =new ArrayList<>();
        alarmList = new ArrayList<>();
        alarmRecordList = new ArrayList<>();
        activeFSensorList = new ArrayList<>();
        activeSSensorList = new ArrayList<>();
        soSafeInitialTime = new Date();
        customerInfo ="Service Contract Id: 800-8008888\n" +
                "Customer name: Queen S Wonderful\n" +
                "Address: 500 El Camino Real,Santa Clara,CA\n";
        monitorPhoneNum ="415-678-9876";
        timers =new ArrayList<>();
    }

    /****************************************************************************************
     * alarm inner class
     *****************************************************************************************/

    public static class Alarm {
        private int sensorId;//ID of the sensor which rings the alarm
        private LocalDateTime time;//alarm happens time
        public String sensorType;// type of sensor which rings the alarm
        public int atArea;// areaID of the alarm happens
        private boolean called;//if true,the alarm hasn't been turned off and called monitor

        public Alarm(Sensor sensor, int sensorId, int areaID){
            //if alarm is created by fire sensor, increase the fire alarm count
            if(sensor instanceof FireSensor)
                fireAlarmCount++;
            //if alarm is created by security sensor, increase the security alarm count
            if(sensor instanceof SecuritySensor)
                securityAlarmCount++;

            this.sensorType = sensor.sensorType;
            this.sensorId =sensorId;
            this.time = LocalDateTime.now();
            this.atArea = areaID;


            //timer to call the moniter if no response after alarm ringings
            timer = new Timer();
            // add this alarm timer to the system timer list, which could holds multiple alarms at the same time
            timers.add(timer);
            TimerTask task =new TimerTask() {

                @Override
                public void run() {
                    try {
                            callMonitor();
                            // cancel task
                            //cancel();
                            //remove all cancelled tasks from this timer's task queue
                            timer.purge();

                    }catch (Exception e){

                    } finally{
                        if(sensorType.equals("Fire"))
                            //change the state of all sensors
                            allActiveFSensorOn =false;
                        else
                            allActiveSSensorOn =false;
                    }
                }
            };

            //if no response within 12 seconds after the alarm is on, callMonitor,only call monitor once
            //not call the police repeatedly
            try{
                timer.schedule(task,1000 *6*2);
            }catch (IllegalArgumentException e){}
        }
        //call monitor
        public void callMonitor(){
            System.out.println("call home owner -"+monitorPhoneNum );
            System.out.println("call - 911");
            called = true;
            //remove this timer from the timers list, but the other timer in the list is still active and might call moniter
            timers.remove(0);
        }

        public String toString(){
            String content =sensorType+" #"+ sensorId +": Area -"+atArea +" call home owner"+monitorPhoneNum+"\ncall - 911";
            return content;
        }
    }


    // turn off the ringing alarm by user
    public void turnOffAlarm(){
        alarmSatus = false;
        alarmList.clear();
        if(timer !=null ||timers.size()!=0) {
            for(Timer t: timers)
                // cancel all the timers task, so it won't call moniter again
                t.cancel();
            //turn of all the ringing alarms' timer if user find the alarm in time and press turn off alarm
            timers.clear();
        }
        setChanged();
        notifyObservers(this);
    }
    // For test use. Randomly choose a sensor , return this sensor to ring the alarm
    public Sensor generateRandomSensor() {
        Random rand = new Random();
        int type =0;
        if(sSensorList.size()!=0 && fSensorList.size()!= 0) {
            type = rand.nextInt(2);
        }
        //if only turn on fire system
        else if(sSensorList.size()==0)
            type = 0; //random number is 0
        else
            //if only turn on security System
            //type is a random number of 1
            type = 1;

        //0 to generate fire alarm

        if (type == 0) {
            //if user choose to turn on all fire sensors each time
            if (allControlPattern == true && timeControlPattern == false) {
                //randomly choose a index from fire sensors list
                int n1 = rand.nextInt(fSensorList.size());

                //return this temp sensor
                FireSensor temp = fSensorList.get(n1);
                System.out.println("The fire sensor " + temp.getFireSensorId() + " raised alarm at Area " +
                        temp.getAtArea());
                return temp;
            }//if user choose to turn on an area's all sensor by schedule area working time
            if (allControlPattern == false && timeControlPattern == true) {
                //randomly choose a index from the active sensors list
                    int n1 = rand.nextInt(activeFSensorList.size());

                //return this temp sensor
                FireSensor temp = fSensorList.get(n1);
                System.out.println("The fire sensor " + temp.getFireSensorId() + " raised alarm at Area " +
                    temp.getAtArea());
                return temp;
            }
        }
        //1 to generate security alarm
        else {
            //if user choose turn on all security sensors each time
            if (allControlPattern == true && timeControlPattern == false) {
                //randomly choose a index from security sensors list
                int n2 = rand.nextInt(sSensorList.size());
                //return this temp sensor
                SecuritySensor temp = sSensorList.get(n2);
//                System.out.println("The security sensor " + temp.getSecuritySensorId() + " raised alarm at Area " +
//                        temp.getAtArea());
                return temp;
            }
            //if user choose to turn on an area's all sensor by schedule area working time
            if (allControlPattern == false && timeControlPattern == true) {
                //randomly choose a index from the active sensors list
                int n2 = rand.nextInt(activeSSensorList.size());
                //return this temp sensor
                SecuritySensor temp = sSensorList.get(n2);
//                System.out.println("The security sensor " + temp.getSecuritySensorId() + " raised alarm at Area " +
//                        temp.getAtArea());
                return temp;
            }
        }
        return null;
    }

    //If alarm rings,alarm status change to true
    public void changeAlarmStatus(Sensor sensor) {
        alarmSatus = true;
        //certain sensor rings the alarm, and the alarm will be added into alarms List
        sensor.raiseAlarm(alarmList, alarmRecordList,this);
        //observe the alarm status's change to notify observers
        setChanged();
        notifyObservers(this);
    }

    /****************************************************************************************
     * Select area to install sensors
     *****************************************************************************************/
    // Add the area which is ready to install any system into areaList of Area
    // Add this area ID into areaIds list of Integer
    public void setSelectArea(int areaID) {
        //create a new area instance
        Area area = new Area(areaID);
        //only add the non-existed areaId into list, ignore the areaID which already exist in areaIds Integer List
        if(!areaIds.contains(areaID)) {
            //Add areaID into areaIds Integer list
            areaIds.add(areaID);
            // Add the area into areaList of Area
            areaList.add(area);

            //notify observers if the areaList, areaIds changed
            setChanged();
            notifyObservers(this);
        }
    }

    // change the layout panel not editable
    public boolean disable(){
        editable = false;
        setChanged();
        notifyObservers(this);
        return editable;
    }

    // change the layout panel editable
    public boolean enable(){
        editable = true;

        setChanged();
        notifyObservers(this);
        return editable;
    }

    //clear the selected area list
    public void clearArea(){
        areaList.clear();
        areaIds.clear();

        setChanged();
        notifyObservers(this);
    }

    // clear all the former installed fire sensors
    public void clearfSensors(){
        activeFSensorList.clear();
        fSensorList.clear();
        fireSensorDistribution.clear();
        Sensor.fSensorCount =0;

        setChanged();
        notifyObservers(this);
    }

    // clear all the former installed security sensors
    public void clearsSensors(){
        activeSSensorList.clear();
        sSensorList.clear();
        securitySensorDistribution.clear();
        Sensor.sSensorCount =0;

        setChanged();
        notifyObservers(this);
    }

    //install fire sensor in certain area
    public void fireSystemInstall(int areaID, int fSensorNum) {
        //list of the fire sensors ID in each room
        int[] sensorIdList = new int[fSensorNum];
        if (fSensorNum != 0) {
            for (int i = 1; i <= fSensorNum; i++) {
                //instantiate sensor and assign area ID to sensor
                Sensor sensor = new FireSensor(areaID);
                //add fire sensor into fire sensors list
                fSensorList.add((FireSensor)sensor);
                //save fire sensors ID
                sensorIdList[i-1] = ((FireSensor)sensor).getFireSensorId();
            }
            //put <area ID, sensors ID list> into hash map
            fireSensorDistribution.put(areaID,sensorIdList);  // for example: room#1 contains sensor #1,2
            //for each area
            for(Area a: areaList) {
                //if the areaID is sent to install fire sensor/sensors
                if (a.getAreaId() == areaID)
                    //set this room fire sensor installed status to true
                    a.setFireSensorInstalled();
            }
            //save the fire system initial time
            fsystemInitialTime = LocalDateTime.now();
        }
        //observe fSensorList's changes
        //observe area sensor installed status' change from the areaList
        setChanged();
        notifyObservers(this);
    }

    //install security sensor in certain area
    public void securitySystemInstall(int areaID, int sSensorNum) {
        //list of the security sensors ID in each room
        int[] sensorIdList =new int[sSensorNum];
        if (sSensorNum != 0) {
            //instantiate sensor and assign area ID to sensor
            for (int i = 1; i <= sSensorNum; i++) {
                //add fire sensor into security sensor list
                Sensor sensor = new SecuritySensor(areaID);
                //add security sensor into the security sensors list
                sSensorList.add((SecuritySensor)sensor);
                //save security sensors ID
                sensorIdList[i-1] = ((SecuritySensor)sensor).getSecuritySensorId();
            }
            //put <area ID, sensors ID list> into hash map
            securitySensorDistribution.put(areaID,sensorIdList);

            for(Area a: areaList) {
                //if the areaID is sent to install security sensor/sensors
                if (a.getAreaId() == areaID)
                    //set this room security sensor installed status to true
                    a.setSecuritySensorInstalled();
            } //save the fire system initial time
            ssystemInitialTime = LocalDateTime.now();
        }
        //observe sSensorList's changes,
        //observe area sensor installed status' change from the areaList
        setChanged();
        notifyObservers(this);
    }

    //get the area that is chosen and highlighted to install sensor system
    public List<Area> getSelectArea() {return areaList; }
    //get the area ID that is chosen and highlighted to install sensor system
    public List<Integer> getSelectAreaIds() {
        return areaIds;
    }
    //get the list of sensors
    public List<FireSensor> getFireSensorList(){
        return fSensorList;
    }
    public List<SecuritySensor> getSecuritySensorList(){
        return sSensorList;
    }

    //get the map of <roomID, sensor IDs list>
    public Map<Integer,int[]> getFireSensorDistribution(){
        return fireSensorDistribution;
    }
    public Map<Integer,int[]> getSecuritySensorDistribution(){
        return securitySensorDistribution;
    }


    /****************************************************************************************
     * Turn on sensors in two different ways
     * - by sensor types: all fire sensors, all security sensors
     * - by area: turn on all sensors of both type which has been installed in selected area
     ****************************************************************************************/
    // when user turns on all the sensors each time
    public void turnOnSensors(String sensorType) throws Exception{
        //if turns on all the security sensors
        if(sensorType.equals("SecuritySensor")) {
            if(sSensorList.size()!=0) {
                //status of all-security-sensor type change to true
                allSecuritySensorOn = true;
                //status of all security sensors are active change to true
                allActiveSSensorOn = true;
                for (SecuritySensor sensor : sSensorList)
                    //turn on all the sensors in security sensors list
                    sensor.turnOnSensor();
            }else{
                throw new Exception("no available sensors");
            }

        }else {
            if(fSensorList.size()!=0) {
                //status of all-fire-sensor type change to true
                allFireSensorOn = true;
                //status of all fire sensors are active change to true
                allActiveFSensorOn = true;
                //turn on all the sensors in fire sensors list
                for (FireSensor sensor : fSensorList)
                    sensor.turnOnSensor();
            }else{
                throw new Exception("no available sensors");
            }
        }
        //observe allActiveFSensorOn/allActiveSSensorOn status's change to notify the lights' color change in areaView
        setChanged();
        notifyObservers(this);
    }

    //when user turns on all the sensors each time
    public void turnOffSensors(String sensorType){
        //if turns off all the security sensors
        if(sensorType.equals("SecuritySensor")) {
            //status of all-security-sensor type change to false
            allSecuritySensorOn = false;
            //status of all security sensors are active change to false
            allActiveSSensorOn = false;
            for (SecuritySensor sensor : sSensorList)
                //turn off all the sensors in security sensors list
                sensor.turnOffSensor();
        } else {
            //status of all-fire-sensor type change to false
            allFireSensorOn = false;
            //status of all fire sensors are active change to false
            allActiveFSensorOn = false;
            //turn off all the sensors in fire sensors list
            for (FireSensor sensor : fSensorList)
                sensor.turnOffSensor();
        }
        //observe allActiveFSensorOn/allActiveSSensorOn status's change to notify the lights' color change in areaView
        setChanged();
        notifyObservers(this);
    }

    //turn on sensors depends on area
    public void turnOnArea(int areaId, int starthr, int endhr, int startmin, int endmin) {
        for (Area area : areaList) {
            //for the area which is selected by user
            if ((area.getAreaId() == areaId)) {
                //set the area start hour
                area.setStartHour(starthr);
                //set the area end hour
                area.setEndHour(endhr);
                area.setStartMin(startmin);
                area.setEndMin(endmin);
            }
        }
        //if the area has installed security sensor
        if (securitySensorDistribution.get(areaId) != null) {
            // for each sensor ID in the area's security sensorIDs List
            for (int sensorID : securitySensorDistribution.get(areaId)) {
                //for each security sensor in the security sensors list
                for (SecuritySensor s : sSensorList) {
                    //if the security sensor ID is in the security sensors List
                    if (s.getSecuritySensorId() == sensorID) {
                        //change the sensor's status to active
                        s.status = true;
                        //add this sensor into active security sensors list
                        activeSSensorList.add(s);
                    }
                }
            }
        }
        //if the area has installed fire sensor
        if (fireSensorDistribution.get(areaId) != null) {
            // for each sensor ID in the area's fire sensorIDs List
            for (int sensorID : fireSensorDistribution.get(areaId)) {
                //for each fire sensor in the fire sensors list
                for (FireSensor s : fSensorList) {
                    //if the fire sensor ID is in the fire sensors List
                    if (s.getFireSensorId() == sensorID) {
                        //change the sensor's status to active
                        s.status = true;
                        //add this sensor into active fire sensors list
                        activeFSensorList.add(s);
                    }
                }
            }
        }
        //observe allActiveFSensorOn/allActiveSSensorOn status's change to notify the lights' color change in areaView
        setChanged();
        notifyObservers(this);
    }

    /****************************************************************************************
     *System install fee
     ****************************************************************************************/

    public int getInstallFee() {
        int fireInstallationFee = 300;
        int fireSensorFee = 100;//each fire sensor
        int securityInstallationFee = 200;
        int securitySensorFee = 50;//each security sensor
        int total = 0;

        //if user has already installed fire sensor system,  get 20% off of the security system install fee
        if (fSensorList.size() != 0 && sSensorList.size() != 0) {
            //system installation fee
            total += securityInstallationFee + .8 * fireInstallationFee;
            //total fee is system fee plus all sensors' fee
            total += fireSensorFee * fSensorList.size() + securitySensorFee * sSensorList.size();
            //output the system initial fee and record
            getInitailCharge(ssystemInitialTime,"Fire system ", "Security System ", total,fSensorList.size() ,sSensorList.size());
        }
        //if user only install fire system
        else if (sSensorList.size() == 0) {
            //total fee is system fee plus all sensors' fee
            total += fireInstallationFee + fireSensorFee * fSensorList.size();
            //output the system install fee and record
            getInitailCharge(fsystemInitialTime,"Fire system ", "", total, fSensorList.size(), 0);
        }
        //if user only install security system
        else {
            //total fee is system fee plus all sensors' fee
            total += securityInstallationFee + securitySensorFee * sSensorList.size();
            //output the system install fee and record
            getInitailCharge(ssystemInitialTime,"", "Security  system ", total, 0, sSensorList.size());
        }
        return total;
    }

    //Output the system installation fee and sensors installation fee
    public void getInitailCharge(LocalDateTime initTime,String fireSys, String secSys,int totalfee,int firesCount,int secusCount){
        file = new File("records.txt");
//        System.out.println(file.getAbsolutePath());
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile(),true));
            //a formatter
            DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            String line = "********************************************************************\n"
                    +"\t\t System Installed: "+ dft.format(initTime)+"\n";
            bufferedWriter.append(line);

            String detail = fireSys + ":" + firesCount + ", "
                    + secSys + ":" + secusCount
                    + " \nSystem install fee: " + totalfee + "\n"
                    + "********************************************************************\n";

            bufferedWriter.append(detail);
            //close the buffer
            bufferedWriter.close();

        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage() +" - Failed to output record");
        }finally{
            System.out.println("Record saved");
        }
    }

    //get alarm charges when a alarm rings
    public int getAlarmCharge(){
        int fireAlarmFee = 50;
        int securityAlarmFee = 20;
        int total=0;

        if (fireAlarmCount != 0) { total += fireAlarmCount * fireAlarmFee; }
        if (securityAlarmCount != 0) { total += securityAlarmCount * securityAlarmFee; }

        return total;
    }

    //when alarm rings, output the alarm records of happens time,area,type,total charge
    public void getAlarmRecords(){
        int fireAlarmFee = 50;
        int securityAlarmFee = 20;
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        try {
            if (file.exists()) {
                BufferedWriter bw= new BufferedWriter(new FileWriter(file,true));
                StringBuilder line = new StringBuilder();

                int charge =0;
                if(alarmRecordList.size()!=0)
                for (Alarm a : alarmRecordList) {
                    if(a.sensorType =="Fire") {
                        charge =fireAlarmFee;
                    }else{
                        charge =securityAlarmFee;
                    }
                    if(a.called) {
                        line.append(dft.format(a.time) + " ," + a.sensorType + " alarm, area - " + a.atArea + " " + "charge:$ " + charge + "\t called police\n");
                    }
                    else
                        line.append(dft.format(a.time) + " ," + a.sensorType + " alarm, area - " + a.atArea + " " + "charge:$ " + charge+"\n") ;
                }

                bw.append(line);
                bw.close();
            }
        }catch(Exception e){
            System.out.println(e.getMessage() +" - Failed to output record");
        }finally{
            //System.out.println("Record saved");
        }
    }

    //when user press the check record button, generate all records and current month total charges
    public void getChargeRecord(){
        // calendar to get the next month beginning date
        Calendar calendar = Calendar.getInstance();
        //set calendar time is now
        calendar.setTime(soSafeInitialTime);
        //get the month next to current month
        calendar.add(Calendar.MONTH, 1);
        //set the date as the first date
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //get the newMonth's starting date
        Date newMonth = calendar.getTime();

        //monthly charge of the alarm charges
        int sum = getAlarmCharge();

        //if today is within system initial month, print initial fee
        if(soSafeInitialTime.before(newMonth))
            sum += getInstallFee();

        //printout details alarm records
        getAlarmRecords();

        try {
            if (file.exists()) {
                //save records to the same file
                BufferedWriter bw= new BufferedWriter(new FileWriter(file,true));
                String line ="------------------------------------Summary---------------------------------\n"
                            +"Current month total: " + sum + "\n"+customerInfo
                           + "----------------------------------------------------------------------------\n";
                bw.append(line);
                bw.close();
            }
        }catch(Exception e){
            System.out.println(e.getMessage() +" - Failed to output record");
        }finally{
            //System.out.println("Record saved");
        }
    }
    //set password
    public void setPassword(char[] password) {
        this.password = password;
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("password.txt")));
            bw.write(password);
            bw.close();
        }catch (IOException e){
            System.out.print(e.getMessage() +" - password output fails");
        }
    }
    //get password
    public char[] getPassword() {
        char[] passwordRecord = null;
        try{
            BufferedReader br = new BufferedReader(new FileReader("password.txt"));
            passwordRecord =br.readLine().toCharArray();
            br.close();
        }catch (IOException e){
            System.out.print(e.getMessage() +" - password intput fails");
        }
        return passwordRecord;
    }
    //verify password
    public boolean checkPassword(char[] password, char[] input) {
        boolean isCorrect = true;

        if (password == null || input.length != password.length) {
            isCorrect = false;
        } else {
            isCorrect = Arrays.equals (input, password);
        }
        //Zero out the password.
        Arrays.fill(password,'0');

        return isCorrect;
    }
}
