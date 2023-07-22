package com.finalproject.allabouthomeuser.models;

import static com.finalproject.allabouthomeuser.models.Lamp.isRoomKindInCategoryList;
import static com.finalproject.allabouthomeuser.models.room.getShade;

import java.util.List;

public class room {
    private double Length;
    private double width;
    private double height;
    private String kind;

    public room(double length, double width, double height, String kind) {

        this.Length = length;
        this.width = width;
        this.height = height;
        this.kind = kind;
    }

    public double getLength() {
        return Length;
    }
    public void setLength(double length) {
        Length = length;
    }
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }
    public String getKind() {
        return kind;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }


    public double getHeight() {
        return height;
    }
        public static int getLux(room room) {
        int lux = 0;
        switch (room.getKind()) {
            case "living room Ambient":
                lux = 110;
                break;
            case "living room Task":
                lux = 300;
                break;
            case "kitchen Ambient":
                lux = 300;
                break;
            case "kitchen Task":
                lux = 650;
                break;
            case "dining room":
                lux = 110;
                break;
            case "bedroom Ambient":
                lux = 110;
                break;
            case "bedroom Task"://
                lux = 300;
                break;
            case "bathroom Ambient":
                lux = 150;
                break;
            case "bathroom Task"://
                lux = 300;
                break;
            case "home office Ambient":
                lux = 150;
                break;
            case "home office Task"://
                lux = 300;
                break;
            case "laundry room":
                lux = 250;
                break;
            default:
                System.out.println("Invalid room name.");
                break;
        }
        return lux;
    }


    public static   String lamphanging(room room){
        String massege =" ";

        if(room.getHeight()<=3&&room.getHeight()>=2.5) {
            massege ="The lamp can be suspended from the ceiling up to 0.5 meters";
        }
        else if((room.getHeight()<2.5&&room.getHeight()>=2.00)||
                (room.getHeight()<2.00&&room.getHeight()>=1.80&&(room.getKind().contains("laundry room")
                        ||room.getKind().contains("bedroom Ambient")))){
            massege ="It is better for the lamp to be close to the ceiling of the room";
        } else if (room.getHeight()>3) {
            massege ="The lamp must hang from the ceiling at least "+ (room.getHeight() - 3) +" meters";

        }
        else {
            massege ="Invalid: This height is less than the ceiling height can be";
        }
        return  massege;
    }



    public static double Ledwatt(room room) {
        double area = room.getLength()*room.getWidth();
        double lux = getLux(room);
        double roomlux= area * lux;
        return  roomlux / 90;//90הוא ייחס וואט ללומן בנורת לד אחת
    }
        public static int numberoflamps(room room ,Lamp lamp) {

        return  (int) (Ledwatt(room)/lamp.getWatt()+1);
    }
    public static boolean Suitablelamps(room room, List<Lamp> lamps) {
        if (lamps.isEmpty())
            System.out.println("EROOOOOOOOR" );
        double totalWattage = 0;
        for (Lamp lamp : lamps) {
            System.out.println("lamp Wattage : " + lamp.getWatt() + " LED Watt");
            totalWattage += lamp.getWatt();
            if (getShade(room) != lamp.getShade())
                return false;
          if (!isRoomKindInCategoryList(room.getKind(),lamp))
               return false;
        }
        double roomWattNeeded = Ledwatt(room);

        System.out.println("Total Wattage of Lamps: " + totalWattage + " LED Watt");
        System.out.println("Room Wattage Needed: " + roomWattNeeded + " LED Watt");

        if (totalWattage < roomWattNeeded || totalWattage >roomWattNeeded*1.5)
            return false;
        return true;
    }




    public static int getShade(room room) {
        String kind = room.getKind();
        if (kind.contains("Ambient")) {
            if (kind.contains("living room") || kind.contains("kitchen") || kind.contains("bedroom")) {
                return 3000;
            } else if (kind.contains("bathroom") || kind.contains("home office")) {
                return 4000;
            }
        } else if (kind.contains("dining room") || kind.contains("laundry room")) {
            return 4000;
        } else if (kind.contains("Task")) {
            if (kind.contains("living room") || kind.contains("bedroom")) {
                return 3000;
            } else if (kind.contains("kitchen")) {
                return 3000;
            } else if (kind.contains("bathroom") || kind.contains("home office")) {
                return 6000;
            }
        }
        return -1;
    }
    public static int getAngle(room room) {
        int Angle;
        String kind = room.getKind();
        if (kind.contains("living room")) {
            Angle = 60;
        } else if (kind.contains("kitchen")) {
            Angle = 25;
        } else if (kind.contains("home office") || kind.contains("dining room")) {
            Angle = 24;
        } else {
            Angle = 45;
        }
        return Angle;
    }





}




