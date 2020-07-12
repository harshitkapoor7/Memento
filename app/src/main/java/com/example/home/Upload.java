package com.example.home;

public class Upload {
    private String imageUri;

    public Upload(){

    }

    public Upload(String uri){
        imageUri=uri;
    }

    public String getImageUri(){
        return imageUri;
    }

    public void setImageUri(String uri){
        imageUri=uri;
    }
}
