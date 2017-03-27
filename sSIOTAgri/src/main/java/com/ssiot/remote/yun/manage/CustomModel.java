package com.ssiot.remote.yun.manage;

public class CustomModel{
//    public String title;
//    public String content;
    private GetCustomShowInterface mGetCustomShowInterface;
    
    public CustomModel(GetCustomShowInterface myGet){
//        this.title = title;
//        this.content = content;
        mGetCustomShowInterface = myGet;
    }
    
    public String getTitle(){
        if (null != mGetCustomShowInterface){
            return mGetCustomShowInterface.getTitle();
        }
        return "";
    }
    
    public String getContent(){
        if (null != mGetCustomShowInterface){
            return mGetCustomShowInterface.getContent();
        }
        return "";
    }
    
    public interface GetCustomShowInterface{
        public String getTitle();
        public String getContent();
    }
}