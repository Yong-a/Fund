package com.example.Fund.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/20 0020.
 */
public class Fund implements Serializable {
    public String fundcode;
    public String fundaliascode;
    public String fundname;
    public String fundnameabbr;
    public int fundtype;
    public String fundinvestmenttypename;
    public String fundcurrdate;
    public float fundnetvalue;
    public float fundtotalnetvalue;
    public float fundincrease;
    public float fundincreasepercent;

    public String getFundname() {
        return fundname;
    }

    public String getFundcode() {
        return fundcode;
    }

    public float getFundnetvalue() {
        return fundnetvalue;
    }

    public String getFundcurrdate() {
        return fundcurrdate;
    }

    public float getFundincreasepercent() {
        return fundincreasepercent;
    }

    public float getFundtotalnetvalue() {
        return fundtotalnetvalue;
    }
}
