package com.finalproject.allabouthomeuser.models;

public class Users {
    private String Address,Birthday,Email,Password,Phone,Username;
    private String ProfilePicUrl;

    public Users(){

    }

    public Users(String username, String email, String phone, String birthday, String address, String password) {
        Address = address;
        Birthday = birthday;
        Email = email;
        Password = password;
        Phone = phone;
        Username = username;
    }

    public static Users instance;
    public static Users getInstance(){
        if(instance==null){
            instance=new Users();
        }
        return instance;
    }


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }
}
