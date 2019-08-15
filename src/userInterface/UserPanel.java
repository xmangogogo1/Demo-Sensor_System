package userInterface;

import components.Sensor;
import soSafeSystem.SoSafe;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public class UserPanel extends JPanel implements Observer {

    private JPasswordField loginField;
    private JButton loginButton;
    private JButton firstButton;

    private JPasswordField signupField;
    private JPasswordField confirmField;
    private JButton signupButton;

    private JComboBox<Integer> comboBox;
    private JComboBox<Integer> areaCombo;
    private JTextField fSensorField;
    private JTextField sSensorField;
    private JButton addFButton;
    private JButton addSButton;
    private JButton finishInstall;
    private JButton backToInstall;

    private JComboBox<Integer> startHourCombo,endHourCombo, startMinCombo, endMinCombo;
    private JPanel timePanel1;
    private JPanel timePanel2;
    private JButton areaOnOff,record,alarmOff;

    private JButton allFireSensors;
    private JButton allSecuritySensors;
    private JButton test,ok;
    private JButton clearList;

    private JPanel loginPanel;
    private JPanel signupPanel;
    private JPanel installPanel;
    private JPanel settingPanel;
    private JPanel allButtons;
    private JPanel schedules;
    private JPanel addTime;
    private JPanel areaPanel;
    private JPanel alarmPanel;
    private JPanel chargePanel;
    private JLabel callLabel;


    private Timer timer;
    private SoSafe soSafe;

    public UserPanel(SoSafe soSafe) {
        if (soSafe == null) {
            throw new NullPointerException();
        }

        this.soSafe = soSafe;

        // format for all panel borders
        Font titleFont = new Font("SansSerif", Font.BOLD, 15);
        Font subtitleFont = new Font("SansSerif", Font.BOLD, 12);
        Color titleColor = Color.BLUE;
        Border blueline = BorderFactory.createLineBorder(Color.BLUE);

        /*******************************************login panel***************************************************/
        loginPanel = new JPanel();
        loginPanel.setSize(50, 600);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
        TitledBorder titledBorder1 = BorderFactory.createTitledBorder(blueline, "User LogIn",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        loginPanel.setBorder(titledBorder1);

        JLabel labelDate = new JLabel( );
        labelDate.setFont(new Font("Arial", Font.BOLD, 12));
        labelDate.setForeground(Color.RED);
        //format the time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ActionListener updateClockAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Assumes clock is a custom components
                LocalDateTime now = LocalDateTime.now();
                //show the current dynamic time
                labelDate.setText("Date: " + formatter.format(now).toString());
            }
        };
        //a timer to update the current time
        Timer t = new Timer(1000, updateClockAction);
        t.start();

        loginField = new JPasswordField(10);
        JLabel loginLabel = new JLabel("Enter the password: ");
        loginLabel.setLabelFor(loginField);
        loginButton = new JButton("  Log In ");
        firstButton = new JButton("Sign Up");

        add(labelDate);
        loginPanel.add(Box.createVerticalStrut(12));
        loginPanel.add(loginLabel);
        loginPanel.add(loginField);
        loginPanel.add(Box.createVerticalStrut(8));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(8));
        loginPanel.add(firstButton);

        /********************************************sign up panel************************************************/

        signupPanel = new JPanel();
        signupPanel.setLayout(new BoxLayout(signupPanel, BoxLayout.PAGE_AXIS));
        TitledBorder titledBorder2 = BorderFactory.createTitledBorder(blueline, "User SignUp",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        signupPanel.setBorder(titledBorder2);

        signupField = new JPasswordField(10);
        JLabel signupLabel = new JLabel("Enter new password: ");
        signupLabel.setLabelFor(signupField);
        confirmField = new JPasswordField(10);
        JLabel confirmLabel = new JLabel("Enter again: ");
        confirmLabel.setLabelFor(confirmField);
        signupButton = new JButton(" Set up ");

        //Creates an invisible, fixed-height =8 components.
        signupPanel.add(Box.createVerticalStrut(8));
        signupPanel.add(signupLabel);
        signupPanel.add(signupField);
        signupPanel.add(Box.createVerticalStrut(8));
        signupPanel.add(confirmLabel);
        signupPanel.add(confirmField);
        signupPanel.add(Box.createVerticalStrut(8));
        signupPanel.add(signupButton);
        signupPanel.setVisible(false);

        /*******************************************install area panel*********************************************/

        installPanel = new JPanel();
        installPanel.setLayout(new BoxLayout(installPanel, BoxLayout.PAGE_AXIS));
        TitledBorder titledBorder3 = BorderFactory.createTitledBorder(blueline, "Install Sensors",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        installPanel.setBorder(titledBorder3);
        JLabel areaSelectLabel = new JLabel("Areas selected");
        comboBox = new JComboBox<>(convertIntegers(soSafe.getSelectAreaIds()));
        areaSelectLabel.setLabelFor(comboBox);

        fSensorField = new JTextField(3);
        JLabel fSensorFieldLabel = new JLabel("Choose fire sensors:");
        fSensorFieldLabel.setLabelFor(fSensorField);

        sSensorField = new JTextField(3);
        JLabel sSensorFieldLabel = new JLabel("Choose security sensors:");
        sSensorFieldLabel.setLabelFor(sSensorField);

        addFButton = new JButton("Install");
        addSButton = new JButton("Install");
        finishInstall = new JButton("Finish");
        backToInstall =new JButton("Back");
        clearList = new JButton("Clear List");


        installPanel.add(Box.createVerticalStrut(10));
        installPanel.add(areaSelectLabel);
        installPanel.add(comboBox);
        installPanel.add(Box.createVerticalStrut(10));
        installPanel.add(fSensorFieldLabel);
        installPanel.add(fSensorField);
        installPanel.add(Box.createVerticalStrut(10));
        installPanel.add(addFButton);
        installPanel.add(Box.createVerticalStrut(10));
        installPanel.add(sSensorFieldLabel);
        installPanel.add(sSensorField);
        installPanel.add(Box.createVerticalStrut(10));
        installPanel.add(addSButton);
        installPanel.add(Box.createVerticalStrut(16));
        installPanel.add(finishInstall);
        installPanel.add(Box.createVerticalStrut(10));
        installPanel.add(clearList);
        installPanel.setVisible(false);

        /************************************setting panel*************************************************/

        settingPanel = new JPanel();
        settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.PAGE_AXIS));
        TitledBorder titledBorder4 = BorderFactory.createTitledBorder(blueline, "Settings",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        settingPanel.setBorder(titledBorder4);

        allButtons = new JPanel();
        allButtons.setLayout(new FlowLayout());
        TitledBorder titledBorder5 = BorderFactory.createTitledBorder(blueline, "All On/Off",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, subtitleFont, titleColor);
        allButtons.setBorder(titledBorder5);

        schedules = new JPanel();
        schedules.setLayout(new GridLayout(5, 1));
        TitledBorder titledBorder6 = BorderFactory.createTitledBorder(blueline, "Schedule On/Off",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, subtitleFont, titleColor);
        schedules.setBorder(titledBorder6);

        alarmPanel = new JPanel();
        alarmPanel.setLayout(new FlowLayout());
        TitledBorder titledBorder7 = BorderFactory.createTitledBorder(blueline, "Alarm control",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, subtitleFont, titleColor);
        alarmPanel.setBorder(titledBorder7);

        chargePanel = new JPanel();
        chargePanel.setLayout(new FlowLayout());
        TitledBorder titledBorder8 = BorderFactory.createTitledBorder(blueline, "Download bill",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, subtitleFont, titleColor);
        chargePanel.setBorder(titledBorder8);

        addTime = new JPanel();
        addTime.setLayout(new GridBagLayout());

        areaPanel = new JPanel();
        areaPanel.setLayout(new FlowLayout());
        JLabel areaLabel =new JLabel("Areas");

        Integer[] hours = new Integer[24];
        Integer[] mins = new Integer[60];
        for(int i = 0; i <24; i++){
            hours[i] =Integer.valueOf(i);
        }
        for (int i = 0; i < 60; i++) {
            mins[i] = Integer.valueOf(i);
        }

        areaCombo =new JComboBox(convertIntegers(soSafe.getSelectAreaIds()));
        areaLabel.setLabelFor(areaCombo);
        areaCombo.setSize(10,10);

        JLabel startHourLabel = new JLabel("Start Hr:Min:");
        JLabel endHourLabel = new JLabel("End Hr:Min:");
        startHourCombo = new JComboBox(hours);
        startHourCombo.setSize(10,10);
        int hour = LocalDateTime.now().getHour();
        startHourCombo.setSelectedItem(hour);
        endHourCombo = new JComboBox(hours);
        endHourCombo.setSize(10,10);
        endHourCombo.setSelectedItem(hour);

        startMinCombo = new JComboBox(mins);
        startMinCombo.setSize(10,10);
        int min = LocalDateTime.now().getMinute();
        startMinCombo.setSelectedItem(min);
        endMinCombo = new JComboBox(mins);
        endMinCombo.setSize(10,10);
        endMinCombo.setSelectedItem(min);

        timePanel1 = new JPanel();
        timePanel1.setLayout(new FlowLayout());
        timePanel1.add(startHourLabel);
        timePanel1.add(startHourCombo);
        timePanel1.add(startMinCombo);

        timePanel2 = new JPanel();
        timePanel2.setLayout(new FlowLayout());
        timePanel2.add(endHourLabel);
        timePanel2.add(endHourCombo);
        timePanel2.add(endMinCombo);

        areaOnOff= new JButton("Add Time");
        areaOnOff.setSize(5,5);

        record = new JButton("Get Records");
        alarmOff = new JButton("Alarm Off");
        alarmOff.setAlignmentX(Component.LEFT_ALIGNMENT);
        allFireSensors = new JButton("FS On");
        allSecuritySensors = new JButton("SS On");
        test = new JButton("Test");
        ok = new JButton("Done");

        allButtons.add(allFireSensors);
        allButtons.add(allSecuritySensors);
        settingPanel.add(allButtons);
        settingPanel.add(Box.createVerticalStrut(10));

        areaPanel.add(areaLabel);
        areaPanel.add(areaCombo);
        schedules.add(areaPanel);
        schedules.add(timePanel1);
        schedules.add(timePanel2);
        addTime.add(areaOnOff);
        schedules.add(addTime);
        settingPanel.add(schedules);
        settingPanel.add(Box.createVerticalStrut(10));

        alarmPanel.add(test);
        alarmPanel.add(alarmOff);
        settingPanel.add(alarmPanel);
        settingPanel.add(Box.createVerticalStrut(10));

        chargePanel.add(record);
        settingPanel.add(chargePanel);
        settingPanel.add(Box.createVerticalStrut(30));

        settingPanel.add(backToInstall);
        settingPanel.add(Box.createVerticalStrut(10));
        settingPanel.add(ok);

        settingPanel.setVisible(false);

        callLabel = new JLabel();
        callLabel.setVisible(false);
        settingPanel.add(callLabel);

        /**********************************************************************************
         * addActionListeners to button
         *********************************************************************************/
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] input = loginField.getPassword();
                boolean result = soSafe.checkPassword(soSafe.getPassword(), input);

                if (result) {
                    JOptionPane.showMessageDialog(UserPanel.this,
                            "Success! You typed the right password.");
                    loginPanel.setVisible(false);
                    installPanel.setVisible(true);
                    soSafe.enable();

                } else {
                    JOptionPane.showMessageDialog(UserPanel.this,
                            "Invalid password. Try again.",
                            "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
                //Zero out the possible password, for security.
                Arrays.fill(input, '0');
                loginField.selectAll();
                resetFocus();
            }
        });

        //first time user enter the soSafeSystem
        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show the sign up panel
                signupPanel.setVisible(true);
            }
        });

        //sign up password and save password into soSafeSystem
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] input1 = signupField.getPassword();
                char[] input2 = confirmField.getPassword();
                boolean result = soSafe.checkPassword(input1, input2);
                if (result) {
                    JOptionPane.showMessageDialog(UserPanel.this,
                            "Success! Your password was set.");
                    signupPanel.setVisible(false);
                    soSafe.setPassword(signupField.getPassword());

                } else {
                    JOptionPane.showMessageDialog(UserPanel.this,
                            "Inconsistent password. Try again.",
                            "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //set fire sensor number for the selected room, install them
        addFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int roomId = Integer.valueOf(comboBox.getSelectedItem().toString());
                    soSafe.fireSystemInstall(roomId, Integer.valueOf(fSensorField.getText()));
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(
                            UserPanel.this,
                            "Please enter a valid amount", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //set security sensor number for the selected room, install them
        addSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int roomId = Integer.valueOf(comboBox.getSelectedItem().toString());
                    soSafe.securitySystemInstall(roomId, Integer.valueOf(sSensorField.getText()));
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(
                            UserPanel.this,
                            "Please enter a valid amount", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //go to setting panel
        finishInstall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                installPanel.setVisible(false);
                settingPanel.setVisible(true);
                soSafe.installDone = true;
            }
        });

        clearList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String message = "Clear all your selections?";
                String title = "clear all";
                int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    soSafe.clearArea();
                    soSafe.clearfSensors();
                    soSafe.clearsSensors();
                }
            }
        });


        //return to install area panel
        backToInstall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                installPanel.setVisible(true);
                settingPanel.setVisible(false);
            }
        });

        // set schedule for each area
        areaOnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soSafe.timeControlPattern = true;
                soSafe.allControlPattern = false;

                int roomId = Integer.valueOf(areaCombo.getSelectedItem().toString());
                soSafe.turnOnArea(roomId, Integer.valueOf(startHourCombo.getSelectedItem().toString()),
                        Integer.valueOf(endHourCombo.getSelectedItem().toString()),
                        Integer.valueOf(startMinCombo.getSelectedItem().toString()),
                        Integer.valueOf(endMinCombo.getSelectedItem().toString()));
            }
        });


        //turn on/off all fire sensors
        allFireSensors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soSafe.allControlPattern = true;
                soSafe.timeControlPattern = false;
                if (soSafe.allFireSensorOn == false) {
                    try {
                        soSafe.turnOnSensors("FireSensor");
                        allFireSensors.setBackground(Color.WHITE);
                        allFireSensors.setText("FS Off");
                    }catch (Exception expt){
                        JOptionPane.showMessageDialog(null,expt.getMessage());
                    }
                } else {
                    soSafe.turnOffSensors("FireSensor");
                    allFireSensors.setBackground(null);
                    allFireSensors.setText("FS On");
                }
            }
        });

        //turn on/off all security sensors
        allSecuritySensors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soSafe.allControlPattern = true;
                soSafe.timeControlPattern = false;
                if (soSafe.allSecuritySensorOn == false) {
                    try {
                        soSafe.turnOnSensors("SecuritySensor");
                        allSecuritySensors.setBackground(Color.WHITE);
                        allSecuritySensors.setText("SS Off");
                    }catch (Exception expt){
                        JOptionPane.showMessageDialog(null,expt.getMessage());
                }
                } else {
                    soSafe.turnOffSensors("SecuritySensor");
                    allSecuritySensors.setBackground(null);
                    allSecuritySensors.setText("SS On");
                }
            }
        });

        //to run random alarm test
        test.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if((soSafe.getActiveSSensorList().size() ==0 && soSafe.getActiveFSensorList().size()==0)){
                    if(!soSafe.timeControlPattern && !soSafe.allFireSensorOn && !soSafe.allSecuritySensorOn) {
                        JOptionPane.showMessageDialog(null, "Please turn on sensors first");
                    }
                    else {
                        Sensor sensor = soSafe.generateRandomSensor();
                        soSafe.changeAlarmStatus(sensor);
                    }
//                }else {
//                    Sensor sensor = soSafe.generateRandomSensor(soSafe);
//                    soSafe.changeAlarmStatus(sensor, soSafe);
//                }
            }
        });

        //output records
        record.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                soSafe.getChargeRecord();
            }
        });

        //turn off alarm
        alarmOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                soSafe.turnOffAlarm();
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                settingPanel.setVisible(false);
                loginPanel.setVisible(true);
                soSafe.disable();
                loginField.setText("");

            }
        });

        setLayout(new FlowLayout());
        add(loginPanel);
        add(signupPanel);
        add(installPanel);
        add(settingPanel);
    }

    //the cursor will focus on the loginField
    protected void resetFocus() {
        loginField.requestFocusInWindow();
    }

    //convert Integer array for input to the comboBox
    public static Integer[] convertIntegers(java.util.List<Integer> integers) {
        Integer[] ret = new Integer[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    public void update(Observable soSafe, Object object) {
        //update comboBox's content
        comboBox.removeAllItems();
        areaCombo.removeAllItems();
        Integer[] areaIdsNew = convertIntegers(((SoSafe) soSafe).getSelectAreaIds());
        for (Integer i : areaIdsNew) {
            comboBox.addItem(i);
            areaCombo.addItem(i);
        }

    }
}
