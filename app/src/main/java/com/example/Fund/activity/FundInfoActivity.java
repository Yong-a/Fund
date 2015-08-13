package com.example.Fund.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Fund.R;
import com.example.Fund.bean.Fund;
import com.example.Fund.bean.NetValue;
import com.example.Fund.utils.Constants;
import com.example.Fund.utils.Tools;

import org.json.JSONObject;

import java.util.ArrayList;

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
    private RadioGroup rgTime;
    private RadioButton rbMonth;
    private RadioButton rbQuarter;
    private RadioButton rbHalfYear;
    private RadioButton rbYear;
    private TextView tvInfoDate;
    private TextView tvInfoNetValue;
    private TextView tvInfoIncrease;
    private ProgressBar pbHtml;
    private String data;
    private ArrayList<NetValue> netValueList;
    private String code;

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
        code = fund.fundcode;
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
        rgTime = (RadioGroup) findViewById(R.id.rg_time);
        rbMonth = (RadioButton) findViewById(R.id.rb_month);
        rbQuarter = (RadioButton) findViewById(R.id.rb_quarter);
        rbHalfYear = (RadioButton) findViewById(R.id.rb_halfyear);
        rbYear = (RadioButton) findViewById(R.id.rb_year);
        rgTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int monthValue = 1;
                if (checkedId == R.id.rb_month) {
                    monthValue = 1;
                } else if (checkedId == R.id.rb_quarter) {
                    monthValue = 3;
                } else if (checkedId == R.id.rb_halfyear) {
                    monthValue = 6;
                } else if (checkedId == R.id.rb_year) {
                    monthValue = 12;
                }
                pbHtml.setVisibility(View.VISIBLE);
                getFundNetValueByCodefinal(code, monthValue);

            }
        });
		tvInfoDate = (TextView) findViewById(R.id.tv_info_date);
		tvInfoDate.setText(fund.fundcurrdate);
		tvInfoNetValue = (TextView) findViewById(R.id.tv_info_netvalue);
		tvInfoNetValue.setText("单位净值:"+Tools.numberFormat(fund.fundnetvalue, 4));
		tvInfoIncrease = (TextView) findViewById(R.id.tv_info_increase);
		String incraseStr = Tools.numberFormat(fund.fundincrease,4);
		if(fund.fundincrease>0){
			incraseStr = "+"+Tools.numberFormat(fund.fundincrease,4);
		}
		tvInfoIncrease.setText("涨跌:"+incraseStr+"("+fundperStr+")");

        pbHtml = (ProgressBar) findViewById(R.id.pb_html);

        getFundNetValueByCodefinal(fund.fundcode, 1);

    }

    public void getFundNetValueByCodefinal(String code, int month) {
        AsyncTask<String, Integer, ArrayList<NetValue>> task = new AsyncTask<String, Integer, ArrayList<NetValue>>() {

            @Override
            protected ArrayList<NetValue> doInBackground(String... params) {
                ArrayList<NetValue> netValueList = null;
                return netValueList;
            }

            @Override
            protected void onPostExecute(ArrayList<NetValue> result) {
                pbHtml.setVisibility(View.GONE);
                if (result == null) {
                    Toast.makeText(FundInfoActivity.this, "数据为空，请稍后查看", Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute(code, month + "");
    }
}
