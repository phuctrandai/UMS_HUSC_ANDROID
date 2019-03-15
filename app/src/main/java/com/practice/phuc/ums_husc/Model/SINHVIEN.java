package com.practice.phuc.ums_husc.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class SINHVIEN {
    private String MaSinhVien;
    private String MatKhau;

    public String getMaSinhVien() {
        return MaSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        MaSinhVien = maSinhVien;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        MatKhau = matKhau;
    }

    public SINHVIEN() {
    }

    public SINHVIEN(String maSinhVien, String matKhau) {
        MaSinhVien = maSinhVien;
        MatKhau = matKhau;
    }

    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MaSinhVien", getMaSinhVien());
            jsonObject.put("MatKhau", getMatKhau());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }
}
