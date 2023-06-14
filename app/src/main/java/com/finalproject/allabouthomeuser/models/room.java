package com.finalproject.allabouthomeuser.models;

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

    public room(double length, double width, String kind) {
        Length = length;
        this.width = width;
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
    public void setHeight(double height) {
        this.height = height;
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



    public static double Ledwatt(room room) {
        double area = room.Length*room.width;
        double lux = getLux(room);
        double roomlux= area * lux;
        return roomlux / 90;//90הוא ייחס וואט ללומן בנורת לד אחת
    }
    public static int numberoflamps(room room ,Lamp lamp) {

        return  (int) (room.Ledwatt(room)/lamp.getWatt()+1);
    }
    public static boolean Suitablelamps(room room , Lamp [] lamp) {//אם המנורות מספיקות לחדר
        double lampswatt=0;
        for(int i=0;i<lamp.length;i++) {
            lampswatt+=lamp[i].getWatt();
        }
        if(room.Ledwatt(room)>=lampswatt)//  אם החדר בצבעים רגילים אז הטווח הוא בין העומצה הצרוכה ועד כ 30% יותר
            //כן, זה נכון. הטווח המומלץ לעוצמת האור בחדר פנים הוא בין העוצמה המינימלית הנדרשת למטרת החדר ועד כ-30% יותר, ובמקרה של חדר עם צבעים כהים הטווח יכול להיות כפול מזה. מומלץ להימנע משימוש בעוצמת אור המוגברת מדי עבור החדר, מכיוון שזה עשוי להיות מפריע וליצור סנוורות וצלליות לא רצויות
            //אם צבעים כהים עד כפול!!לשים לב למקרה של אור חזק מדי (סנוור)
            return true;
        return false;
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




