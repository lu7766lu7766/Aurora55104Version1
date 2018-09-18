package com.mac.jacwang.aurora20150406;

import java.io.Serializable;

public class Poi implements Serializable
{
    private String brand_name = "";      //景點店家名稱
    private String sub_name = "";        //景點店家區域名稱
    private String address = "";         //景點店家住址
    private String phone = "";           //景點店家電話
    private String logo = "";            //圖片
    private String menu = "";            //圖片
    private int    id;              //fi_id
    private double latitude;        //景點店家緯度
    private double longitude;       //景點店家經度
    private double distance;        //景點店家距離

    //建立物件時需帶入景點店家名稱、景點店家緯度、景點店家經度
    public Poi(String brand_name , double latitude , double longitude)
    {
        //將資訊帶入類別屬性
        this.brand_name = brand_name ;
        this.latitude = latitude ;
        this.longitude = longitude ;
    }

    //建立物件時需帶入景點店家名稱、景點店家緯度、景點店家經度
    public Poi(String brand_name , String address)
    {
        //將資訊帶入類別屬性
        this.brand_name = brand_name ;
        this.address = address ;
    }
    //建立物件時需帶入景點店家名稱、景點店家緯度、景點店家經度
    public Poi(int id )
    {
        this.id = id;
    }

    public void setId(int id){this.id = id;}
    public int getId(){return id;}
    //取得店家名稱
    public String getBrandName()
    {
        return brand_name;
    }
    public void setBrandName(String brand_name)
    {
        this.brand_name = brand_name;
    }
    //取得店家名稱
    public String getSubName()
    {
        return sub_name;
    }

    public void setSubName(String sub_name)
    {
        this.sub_name = sub_name;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }
    //取得店家緯度
    public String getAddress()
    {
        return address;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    //取得店家緯度
    public String getPhone()
    {
        return phone;
    }

    //取得店家LOGO
    public String getMenu()
    {
        return menu;
    }
    public void setMenu(String menu)
    {
        this.menu = menu;
    }

    public String getLogo()
    {
        return logo;
    }
    public void setLogo(String logo)
    {
        this.logo = logo;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }
    //取得店家緯度
    public double getLatitude()
    {
        return latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }
    //取得店家經度
    public double getLongitude()
    {
        return longitude;
    }

    //寫入店家距離
    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    //取的店家距離
    public double getDistance()
    {
        return distance;
    }

    public void setcoordinate(int id,double latitude,double longitude)
    {
//        new DBConnector(null,
//                "update t_store " +
//                        "set ff_latitude = '"+latitude+"' , " +
//                            "ff_longitude = '"+longitude+"' " +
//                        "where fi_id="+id,-1);

//        this.latitude = latitude;
//        this.longitude = longitude;
    }
}