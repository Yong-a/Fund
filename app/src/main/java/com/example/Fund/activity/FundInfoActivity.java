package com.example.Fund.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.Fund.R;
import com.example.Fund.bean.Fund;
import com.example.Fund.utils.Constants;
import com.example.Fund.utils.Tools;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/7/20 0020.
 */

public class FundInfoActivity extends Activity {

    private TextView tvFundname;
    private TextView tvFundcode;
    private TextView tvFundinvestmenttypename;
    private TextView tvFundrisk;
    private TextView tvFundvalue;
    private TextView tvFundtotalnetvalue;
    private TextView tvFundincreasepercent;
    private TextView tvFundDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_info);
        tvFundname = (TextView) findViewById(R.id.tv_fundname);
        tvFundcode = (TextView) findViewById(R.id.tv_fundcode);
        tvFundinvestmenttypename = (TextView) findViewById(R.id.tv_fundinvestmenttypename);
        tvFundrisk = (TextView) findViewById(R.id.tv_fundrisk);
        tvFundvalue = (TextView) findViewById(R.id.tv_fundvalue);
        tvFundtotalnetvalue = (TextView) findViewById(R.id.tv_fundtotalnetvalue);
        tvFundincreasepercent = (TextView) findViewById(R.id.tv_fundincreasepercent);
        Intent intent = getIntent();
        Fund fund = (Fund) intent.getSerializableExtra("fund");
        tvFundname.setText(fund.fundname);
        tvFundcode.setText(fund.fundcode);
        tvFundinvestmenttypename.setText(fund.fundinvestmenttypename);
        String fundRisk = "高风险";
        try {
            JSONObject jo = new JSONObject(Constants.TYPE_TO_RISK);
            fundRisk = jo.getString(fund.fundinvestmenttypename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvFundrisk.setText(fundRisk);
        tvFundvalue.setText(Tools.numberFormat(fund.fundnetvalue, 4));
        tvFundtotalnetvalue.setText(Tools.numberFormat(fund.fundtotalnetvalue, 4));
        String fundperStr = Tools.numberFormat((fund.fundincreasepercent * 100), 2);
        int colorStr;
        if (fund.fundincreasepercent > 0) {
            fundperStr = "+" + fundperStr + "%";
            colorStr = 0xFFFF0000;
        } else if (fund.fundincreasepercent < 0) {
            fundperStr = fundperStr + "%";
            colorStr = 0xFF00FF00;
        } else {
            fundperStr = fundperStr + "%";
            colorStr = 0xFF808080;
        }
        tvFundincreasepercent.setText(fundperStr);
        tvFundincreasepercent.setTextColor(colorStr);
        tvFundDate = (TextView) findViewById(R.id.tv_funddate);
        tvFundDate.setText("日期:" + fund.fundcurrdate);
        String incraseStr = Tools.numberFormat(fund.fundincrease, 4);
        if (fund.fundincrease > 0) {
            incraseStr = "+" + Tools.numberFormat(fund.fundincrease, 4);
        }
    }
}
