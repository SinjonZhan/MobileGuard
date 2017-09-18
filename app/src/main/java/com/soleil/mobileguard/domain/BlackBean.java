package com.soleil.mobileguard.domain;

public class BlackBean {

    private String phone;
    private int mode;

    public BlackBean(String phone, int mode) {
        this.phone = phone;
        this.mode = mode;
    }

    public BlackBean(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlackBean blackBean = (BlackBean) o;

        return phone.equals(blackBean.phone);

    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }

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
    public String toString() {
        return "BlackBean{" +
                "phone='" + phone + '\'' +
                ", mode=" + mode +
                '}';
    }
}
