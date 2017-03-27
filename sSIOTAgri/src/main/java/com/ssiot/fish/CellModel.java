package com.ssiot.fish;

public class CellModel{
    public static final int MODE_URL = 1;
    public static final int MODE_LIST = 2;
    public static final int MODE_OTHER = 3;
    public int itemImage;
    public String itemText = "";
    public String itemId = "";
    
    public int openType = 0;
    public String urlString = "";
    
    public CellModel(int itemImage, String itemText, String itemId){
        this.itemImage = itemImage;
        this.itemText = itemText;
        this.itemId = itemId;
    }
    
    public CellModel(int itemImage, String itemText, String itemId, int mode){
        this.itemImage = itemImage;
        this.itemText = itemText;
        this.itemId = itemId;
        openType = mode;
    }
    
    public CellModel setUrl(String url){
        openType = MODE_URL;
        urlString = url;
        return this;
    }
}