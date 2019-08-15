package userInterface;

import soSafeSystem.Area;
import soSafeSystem.SoSafe;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ting Wang, Xiaoman Wang on 5/30/17.
 */
public class AreaView extends JPanel implements Observer {
    //this Observer contains a SoSafe model
    private SoSafe soSafe;
    //arrays of userInterface as room layoout graph
    JPanel[] areaPanels = new JPanel[6];
    //arrays of areaButtons on areaPanels which could select room
    JButton[] areaButtons = new JButton[6];
    //Oval object as the fire Sensor status to show installed / working status
    public DrawOval[] areafs = new DrawOval[6];
    //Oval object as the security Sensor status to show installed / working status
    public DrawRect[] areass = new DrawRect[6];
    //label for show the installed fire sensor's IDs in a room
    public JLabel[] fs = new JLabel[6];
    //label for show the installed security sensor's IDs in a room
    public JLabel[] ss =new JLabel[6];

    public AreaView(SoSafe soSafe) {
        //throw exception if soSafe is not exist
        if (soSafe == null) {
            throw new NullPointerException();
        }
        //set this soSafe model equals to the model passed in the constructor
        this.soSafe = soSafe;
        //2 rows, 3 cols layout
        setLayout(new GridLayout(2, 3));

        for (int i = 0; i < 6; i++) {
            //create panel
            areaPanels[i] = new JPanel();
            //create button
            areaButtons[i] = new JButton("   Area " + Integer.toString(i) + "   ");
            areaButtons[i].setEnabled(false);
            areaPanels[i].setBorder(BorderFactory.createRaisedBevelBorder());
            areaPanels[i].setLayout(new BoxLayout(areaPanels[i], BoxLayout.PAGE_AXIS));
            //add button on panel
            areaPanels[i].add(areaButtons[i]);
            //draw oval object
            areafs[i] = new DrawOval(Color.WHITE);
            //add oval object on panel
            areaPanels[i].add(areafs[i]);
            //set the oval object invisible
            areafs[i].setVisible(false);

            //draw rectangle object
            areass[i] = new DrawRect(Color.WHITE);
            //add rectangle object on panel
            areaPanels[i].add(areass[i]);

            //set the rectangle object invisible
            areass[i].setVisible(false);

            //create labels
            fs[i] =new JLabel();
            ss[i] =new JLabel();

            //add labels on panel
            areaPanels[i].add(fs[i]);
            areaPanels[i].add(ss[i]);

            //refer areaPanel to curPanel so that could access this curPanel in ActionListener
            JPanel curPanel = areaPanels[i];
            //refer areaButton to curButton so that could access this curButton in ActionListener
            JButton curButton = areaButtons[i];

            //refer this areaID i to curID
            int curId = i;

            areaButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //Set the panel with lowerBevelBorder when clicked
                    curPanel.setBorder(BorderFactory.createLoweredBevelBorder());
                   //Set the panel background color change to gray when clicked
                    curPanel.setBackground(Color.lightGray);
                    //Set the button background color change to orange when clicked
                    curButton.setBackground(Color.orange);
                    //soSafe model record this area as selected area
                    soSafe.setSelectArea(curId);
                }
            });

            // add areaPanel on this view
            add(areaPanels[i]);
        }

        Font titleFont = new Font("SansSerif", Font.BOLD, 15);
        Color titleColor = Color.BLACK;
        TitledBorder titledBorder = BorderFactory.createTitledBorder(null, "Area Layout",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        setBorder(titledBorder);

    }

    /*********************************************************************************************************
     * DrawOval JComponent with color as parameter
     *******************************************************************************************************/
    public class DrawOval extends JComponent {
        public Color color;

        public DrawOval(Color color) {
            //set oval preferred size
            setPreferredSize(new Dimension(30, 30));
            //set color
            this.color = color;
        }

        @Override
        public void paintComponent(Graphics g) {
            // override super paintComponent constructor
            super.paintComponent(g);
            //set the start x coordinates as 2/3 of the areaView's width
            int width = getWidth() * 2 / 3;
            //set the start y coordinates as 1/3 of the areaView's width
            int height = getHeight() / 3;
            //set color
            g.setColor(color);
            //fill the Oval start from(x,y) with width 30,height 30
            g.fillOval(width, height, 30, 30);
        }
    }
    /*********************************************************************************************************
     * DrawRect JComponent with color as parameter
     *******************************************************************************************************/
    public class DrawRect extends JComponent {
        public Color color;

        public DrawRect(Color color){
            //set oval preferred size
            setPreferredSize(new Dimension(30, 30));
            //set color
            this.color = color;
        }

        @Override
        public void paintComponent(Graphics g){
            // override super paintComponent constructor
            super.paintComponent(g);
            //set the start x coordinates as 2/3 of the areaView's width
            int width = getWidth() * 2 / 3;
            //set the start y coordinates as 1/3 of the areaView's width
            int height = getHeight() / 3;
            //set color
            g.setColor(color);
            //fill the Oval start from(x,y) with width 30,height 30
            g.fillRect(width, height, 30, 30);
        }
    }

    /**********************************************************************************************************
     * Observer will update following components if model call notifyObservers()
     **********************************************************************************************************/
    public void update(Observable soSafe, Object object) {

        validate();
        //if not install Fire Sensor
        if(((SoSafe) soSafe).getFireSensorList().size() ==0){
            for (int i = 0; i < 6; i++) {
                //hide the buttons from panel
                areafs[i].setVisible(false);
                //set sensorID labels to empty
                fs[i].setText("");
            }
            repaint();
        }

        //if not install security Sensor
        if(((SoSafe) soSafe).getSecuritySensorList().size() ==0){
            for (int i = 0; i < 6; i++) {
                //hide the buttons from panel
                areass[i].setVisible(false);
                //set sensorID labels to empty
                ss[i].setText("");
            }
            repaint();
        }

        //if press the clearList button, SelectAreaIds list is empty
        if(((SoSafe) soSafe).getSelectAreaIds().size() == 0){
            for (int i = 0; i < 6; i++) {
                //Set the button background color change to original when clicked
                areaPanels[i].setBackground(null);
                areaPanels[i].setBorder(BorderFactory.createRaisedBevelBorder());
                areaButtons[i].setBackground(null);

                //hide the buttons from panel
                areafs[i].setVisible(false);
                areass[i].setVisible(false);
                fs[i].setText("");
                ss[i].setText("");
            }
            repaint();
        }

        //if the area layout panel is editable in install panel
        if(((SoSafe) soSafe).editable){
            for (int i = 0; i < 6;i++){
                areaButtons[i].setDefaultCapable(true);
                areaButtons[i].setEnabled(true);
            }
            repaint();
        }

        //the area Layout is not editable when use back to the first page
        if(!((SoSafe) soSafe).editable){
            for (int i = 0; i < 6;i++){
                areaButtons[i].setDefaultCapable(false);
                areaButtons[i].setEnabled(false);
            }
            repaint();
        }

        // when fire sensors are installed, make the sensor visible
        for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
            //if the areaID is contained in FireSensorDistribution hashmap
            if (((SoSafe) soSafe).getFireSensorDistribution().containsKey(i)) {
               //DrawOval components is set visible
                areafs[i].setVisible(true);
                areafs[i].setBackground(null);
            }
        }

        // when security sensors are installed, make the sensor visible
        for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
            //if the areaID is contained in securitySensorDistribution hashmap
            if (((SoSafe) soSafe).getSecuritySensorDistribution().containsKey(i)) {
                //DrawRec components is set visible
                areass[i].setVisible(true);
                areass[i].setBackground(null);
            }
        }

        //if the sensor soSafeSystem installation has done
        if (((SoSafe) soSafe).installDone) {
            //for each area has been selected
            for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
                //if this area installed any fire sensor
                if (((SoSafe) soSafe).getFireSensorDistribution().containsKey(i)) {
                    //show the sensor IDs installed in each area
                    fs[i].setText("F: " +
                    Arrays.toString(((SoSafe) soSafe).getFireSensorDistribution().get(i)));
                    //set label for DrawOval components
                    fs[i].setLabelFor(areafs[i]);
                    areaPanels[i].add(fs[i]);
                }

                //if this area installed any security sensor
                if (((SoSafe) soSafe).getSecuritySensorDistribution().containsKey(i)) {
                    //show the sensor IDs installed in each area

                    ss[i].setText("S: " +
                            Arrays.toString(((SoSafe) soSafe).getSecuritySensorDistribution().get(i)));
                    //set label for DrawRec components
                    ss[i].setLabelFor(areass[i]);
                    areaPanels[i].add(ss[i]);
                }

                ((SoSafe) soSafe).installDone = false;

                repaint();
            }
        }

        // when fire soSafeSystem is activated, and user true turn on all fire Sensors, all fire sensors turn green
        if (((SoSafe) soSafe).allControlPattern && ((SoSafe) soSafe).allFireSensorOn) {
            //for each areaID
            for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
                int curid = i;
                //if fireSensorDistribution hashmap contains this areaID key
                if (((SoSafe) soSafe).getFireSensorDistribution().containsKey(curid)) {
                    //this area's DrawOval's color turns green
                    areafs[curid].color = Color.GREEN;
                }
            }
            repaint();

        } else {// if user not choose turn on all fire sensors or fire sensors are not activated
            //for each areaID
            for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
                int curid = i;
                //if fireSensorDistribution hashmap contains this areaID key
                if (((SoSafe) soSafe).getFireSensorDistribution().containsKey(curid)) {
                    //this area's DrawOval's color turns white
                    areafs[curid].color = Color.WHITE;
                }
            }
            repaint();
        }

        // when security soSafeSystem is activated, and user true turn on all security Sensors, all security sensors turn green
        if (((SoSafe) soSafe).allControlPattern && ((SoSafe) soSafe).allSecuritySensorOn) {
            for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
                int curid = i;
                if (((SoSafe) soSafe).getSecuritySensorDistribution().containsKey(curid)) {
                    areass[curid].color = Color.GREEN;
                }
            }
            repaint();

        } else { // if user not choose turn on all security sensors or fire sensors are not activated,all lights trun white
            for (int i : ((SoSafe) soSafe).getSelectAreaIds()) {
                int curid = i;
                if (((SoSafe) soSafe).getSecuritySensorDistribution().containsKey(curid)) {
                    areass[curid].color = Color.WHITE;
                }
            }
            repaint();
        }

        //when area time range is selected, and current time is tested in the range, all sensors in the room turn green.
        if (((SoSafe) soSafe).timeControlPattern) {
            int hr = LocalDateTime.now().getHour();
            int min = LocalDateTime.now().getMinute();
            for (Area area : ((SoSafe) soSafe).getSelectArea()) {
                //if the current hour is between startHour and EndHour
                //here the minute test is not simply larger then start and smaller than end
                if ((area.getStartHour() == area.getEndHour()) && (hr == area.getStartHour()) &&
                        (min >= area.getStartMin() && min <= area.getEndMin())) {
                    areafs[area.getAreaId()].color = Color.GREEN;
                    areass[area.getAreaId()].color = Color.GREEN;
                }

                if ( (hr >= area.getStartHour() && hr <= area.getEndHour()) && min >= area.getStartMin()) {
                    //the selected area's sensors components trun green
                    areafs[area.getAreaId()].color = Color.GREEN;
                    areass[area.getAreaId()].color = Color.GREEN;
                }
                if ((hr > area.getEndHour()) || ( (hr == area.getEndHour()) && (min >= area.getEndMin())) ) {
                    if (areafs[area.getAreaId()].color == Color.GREEN) {
                        areafs[area.getAreaId()].color = Color.WHITE;
                    }

                    if (areass[area.getAreaId()].color == Color.GREEN) {
                        areass[area.getAreaId()].color = Color.WHITE;
                    }
                }
            }
            repaint();
        }

        //If there is any alarm rings
        if (((SoSafe) soSafe).alarmSatus) {
            //for each alarm in alarmList
            for (SoSafe.Alarm alarm: ((SoSafe) soSafe).alarmList) {
                //get the area ID the alarm appears
                int curid = alarm.atArea;
                //if the alarm is a Fire sensor alarm
                if (alarm.sensorType == "Fire") {
                    //this area's fire light turns red
                    areafs[curid].color = Color.red;
                } else {
                    //this area's security light turns red
                    areass[curid].color = Color.red;
                }
            }
            repaint();
        }
    }
}