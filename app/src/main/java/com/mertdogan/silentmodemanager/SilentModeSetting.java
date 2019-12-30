package com.mertdogan.silentmodemanager;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class SilentModeSetting implements Serializable {
    private static final long serialVersionUID = 1L;
    String startTime;
    String endTime;
    List<Integer> days;
    String mode;
    int id;
    int idLoc;
    String title;
    LatLng location;
    int setType;

    public SilentModeSetting(String startTime,
                             String endTime,
            List<Integer> days,
            String mode,
            String title)
    {
        this.startTime=startTime;
        this.endTime=endTime;
        this.days=days;
        this.mode=mode;
        this.title=title;

    }

    public SilentModeSetting(LatLng location,
                              String title)
    {
        this.location=location;
        this.title=title;

    }



    public String getStartTime(){
        return startTime;
    }
    public String getEndTime(){
        return endTime;
    }

    public List<Integer> getDays(){
        return days;
    }

    public String getMode(){
        return mode;
    }

    public int getId(){
        return id;
    }
    public int getIdLoc(){
        return idLoc;
    }
    public int getSetType(){
        return setType;
    }
    public LatLng getLocation(){return location;}
    public String getTitle(){
        return title;
    }

    public void setStartTime(String startTime){
        this.startTime=startTime;
    }
    public void setEndTime(String endTime){
        this.endTime=endTime;
    }

    public  void setDays(List<Integer> days){
        this.days=days;
    }

    public void setMode(String mode){
        this.mode=mode;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public void setId(int id){
        this.id=id;
    }
    public void setIdLoc(int idLoc){
        this.idLoc=idLoc;
    }
    public void setSetType(int setType){
        this.setType=setType;
    }

    @Override
    public String toString(){
        if(this.setType==0)
            return title + "(time)";
        if(this.setType==1)
            return title + "(location)";
        else
            return title;
    }


}
