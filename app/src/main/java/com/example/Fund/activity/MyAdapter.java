package com.example.Fund.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.Fund.R;
import com.example.Fund.bean.Fund;
import com.example.Fund.utils.Tools;

import java.util.List;

/**
 * Created by Administrator on 2015/7/20 0020.
 */
public class MyAdapter extends ArrayAdapter {
    private int resourceId;
    private int netValueType;
    private List<Fund> list;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    public MyAdapter(Context context, int resource, List<Fund> objects, int netValueType) {
        super(context, resource, objects);
        this.list = objects;
        this.netValueType = netValueType;
        resourceId = resource;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Fund fund = (Fund) getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.cb_toggle_favorite);
            viewHolder.tv_fundname = (TextView) view.findViewById(R.id.FundName);
            viewHolder.tv_fundcode = (TextView) view.findViewById(R.id.FundCode);
            viewHolder.tv_fundnetvalue = (TextView) view.findViewById(R.id.FundNetValue);
            viewHolder.tv_fundcurrdate = (TextView) view.findViewById(R.id.FundCurrDate);
            viewHolder.tv_fundincreasepercent = (TextView) view.findViewById(R.id.FundInCreasePercent);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_fundname.setText(fund.getFundname());
        viewHolder.tv_fundcode.setText(fund.getFundcode());
        if (netValueType == 0) {
            viewHolder.tv_fundnetvalue.setText(Tools.numberFormat(fund.getFundnetvalue(), 4));
        } else {
            viewHolder.tv_fundnetvalue.setText(Tools.numberFormat(fund.getFundtotalnetvalue(), 4));
        }
        viewHolder.tv_fundcurrdate.setText(fund.getFundcurrdate());
        String fundIncreasePercent = Tools.numberFormat((fund.getFundincreasepercent() * 100), 2);
        String fipStr;
        if (fund.getFundincreasepercent() > 0) {
            viewHolder.tv_fundincreasepercent.setBackgroundResource(R.drawable.zhang);
            fipStr = "+" + fundIncreasePercent + "%";
        } else if (fund.getFundincreasepercent() < 0) {
            viewHolder.tv_fundincreasepercent.setBackgroundResource(R.drawable.die);
            fipStr = fundIncreasePercent + "%";
        } else {
            viewHolder.tv_fundincreasepercent.setBackgroundResource(R.drawable.pin);
            fipStr = fundIncreasePercent + "%";
        }
        viewHolder.tv_fundincreasepercent.setText(fipStr);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    editor.putBoolean(fund.getFundcode(), true);
                    editor.commit();
                } else {
                    editor.putBoolean(fund.getFundcode(), false);
                    editor.commit();
                }
            }
        });
        Boolean isChecked = pref.getBoolean(fund.getFundcode(), false);
        viewHolder.checkBox.setChecked(isChecked);
        return view;
    }

    class ViewHolder {
        CheckBox checkBox;
        TextView tv_fundname;
        TextView tv_fundcode;
        TextView tv_fundnetvalue;
        TextView tv_fundcurrdate;
        TextView tv_fundincreasepercent;
    }
}
