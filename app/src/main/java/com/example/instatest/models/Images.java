package com.example.instatest.models;

public class Images {

    private Standard_res standard_res;

    public Standard_res getStandard_res(){
        return standard_res;
    }


    public class Standard_res{
        private String url;
        public String getUrl() {
            return url;
        }
    }

}
