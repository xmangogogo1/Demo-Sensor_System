import soSafeSystem.Area;
import soSafeSystem.SoSafe;
import userInterface.AreaView;
import userInterface.UserPanel;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
/**
 * COEN 275 - Gourp7 SoSafe security & fire detect soSafeSystem
 *            Created by Ting Wang, Xiaoman Wang on 5/30/17.
 * *
 * * Design methods:
 * * MVC, Multi-thread, Object Oriented Design
 *
 * * Funationalities:
 * * basic: User signUp/login;
 *          View current time;
 *
 * * Installation: Able to select rooms;
 *                 Clear the former settings without turnoff soSafeSystem;
 *                 Install two types of sensors, view the corresponding sensor IDs in different areas;
 * * schedule: Set start/end hour/minutes to automatically turn on/off sensor soSafeSystem by area;
 *
 * * Alarms: Raise multiple alarms at the same time, capable of turn off all the ringing alarms by one press;
 *           Call moniters without turn off alarm by user;
 *           Won't call moniters if turn off alarm in time;
 *           Alarm random test;
 *
 * * Records:Get current month charges;
 */
public class SystemManager extends JFrame {
    SoSafe soSafe = new SoSafe();
    AreaView areaView;

    public SystemManager(SoSafe soSafe) {
        super("SoSafe Home System");
        this.soSafe = soSafe;
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

        //observer userPanel with login panel,sign up panel,install panel,setting panel
        UserPanel userPanel = new UserPanel(soSafe);
        userPanel.setPreferredSize(new Dimension(100, 600));
        //add this observer to model soSafe
        soSafe.addObserver(userPanel);

        //observer userPanel with areaLayout panel
        areaView = new AreaView(soSafe);
        //add this observer to model soSafe
        soSafe.addObserver(areaView);

        //add userInterface to frame
        contentPane.add(userPanel);
        contentPane.add(areaView);
        setSize(800, 550);
        setLocationRelativeTo(null);
    }

    //Runnable class for the schedule of sensors
    public class SchedulerRunnable implements Runnable {

        public SchedulerRunnable(SoSafe soSafe) {}

        //when the SchedulerRunnable is passed to Thread Class, this thread will check the sensor schedule and
        //turn on/off the sensors in certain area(s) at the certain schedule time
        @Override
        public void run() {
            while (true) {
                if (((SoSafe) soSafe).timeControlPattern) {
                    int hr = LocalDateTime.now().getHour();
                    int min = LocalDateTime.now().getMinute();
                    for (Area area : ((SoSafe) soSafe).getSelectArea()) {
                        if ((area.getStartHour() == area.getEndHour()) && (hr == area.getStartHour()) &&
                                (min >= area.getStartMin() && min <= area.getEndMin())) {
                            if (areaView.areafs[area.getAreaId()].color == Color.WHITE) {
                                areaView.areafs[area.getAreaId()].color = Color.GREEN;
                            }
                            if (areaView.areass[area.getAreaId()].color == Color.WHITE) {
                                areaView.areass[area.getAreaId()].color = Color.GREEN;
                            }
                        }

                        if ( (hr >= area.getStartHour() && hr <= area.getEndHour())
                                && min >= area.getStartMin()) {
                            if (areaView.areafs[area.getAreaId()].color == Color.WHITE) {
                                areaView.areafs[area.getAreaId()].color = Color.GREEN;
                            }
                            if (areaView.areass[area.getAreaId()].color == Color.WHITE) {
                                areaView.areass[area.getAreaId()].color = Color.GREEN;
                            }
                        }

                        if ((hr > area.getEndHour()) || ( (hr == area.getEndHour()) && (min >= area.getEndMin())) ) {
                            if (areaView.areafs[area.getAreaId()].color == Color.GREEN) {
                                areaView.areafs[area.getAreaId()].color = Color.WHITE;
                            }

                            if (areaView.areass[area.getAreaId()].color == Color.GREEN) {
                                areaView.areass[area.getAreaId()].color = Color.WHITE;
                            }
                        }
                    }
                    repaint();
                }
                try {
                    //check it every second
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        SoSafe soSafe = new SoSafe();
        SystemManager manager = new SystemManager(soSafe);

        //scheduler thread for the sensor schedules
        Thread scheduler = new Thread(manager.new SchedulerRunnable(soSafe));
        scheduler.start();

        manager.setDefaultCloseOperation(EXIT_ON_CLOSE);
        manager.setVisible(true);
    }
}
