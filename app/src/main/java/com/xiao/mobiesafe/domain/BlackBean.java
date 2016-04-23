package com.xiao.mobiesafe.domain;


public class BlackBean {
    private String phone;
    private int mode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BlackBean){
            BlackBean bo=(BlackBean)o;
            return  bo.phone.equals(this.phone);
        }

        return false;
    }
}
