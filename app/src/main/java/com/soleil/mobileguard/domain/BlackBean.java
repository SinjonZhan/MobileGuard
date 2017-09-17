package com.soleil.mobileguard.domain;

public class BlackBean {
   private String Phone;

    private int mode;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackBean{" +
                "Phone='" + Phone + '\'' +
                ", mode=" + mode +
                '}';
    }
}
