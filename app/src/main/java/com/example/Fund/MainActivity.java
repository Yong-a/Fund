package com.example.Fund;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Fund.activity.FundInfoActivity;
import com.example.Fund.activity.MyAdapter;
import com.example.Fund.bean.Fund;
import com.example.Fund.utils.Constants;
import com.example.Fund.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {
    private int pageNo = 1;
    private int pageCount = 0;
    private String typeName = "All";
    private ListView lv_fundinfo;
    private TextView tvPage;
    private RadioButton radioButton;
    private Button btPrev;
    private Button btNext;
    private Button btAll;
    private Button btStock;
    private Button btMixed;
    private Button btBond;
    private Button btCurrency;
    private Button btNetValue;
    private ImageView ivNetValue;
    private ImageView ivRange;
    private ArrayList<Fund> fundList = null;
    private ArrayList<Fund> fundList2 = null;
    private int netValueType = 0;//净值类型，0为最新净值，1为累计净值
    private int rangeType = 0;//涨跌类型 0为降序, 1升序
    private SwipeRefreshLayout mSwipeLayout;
    private RadioGroup rgFundType;
    private ProgressBar pbList;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_fundinfo = (ListView) findViewById(R.id.lv_fundinfo);
        tvPage = (TextView) findViewById(R.id.tv_page);
        btPrev = (Button) findViewById(R.id.bt_prev_page);
        btNext = (Button) findViewById(R.id.bt_next_page);
        btAll = (Button) findViewById(R.id.rb_all);
        btStock = (Button) findViewById(R.id.rb_stock);
        btMixed = (Button) findViewById(R.id.rb_mixed);
        btBond = (Button) findViewById(R.id.rb_bond);
        btCurrency = (Button) findViewById(R.id.rb_currency);
        btNetValue = (Button) findViewById(R.id.bt_netvalue);
        ivNetValue = (ImageView) findViewById(R.id.iv_netValue);
        ivRange = (ImageView) findViewById(R.id.iv_range);
        pbList = (ProgressBar) findViewById(R.id.pb_list);
        rgFundType = (RadioGroup) findViewById(R.id.rg_type);
        goPage(pageNo);
        rgFundType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        clickTypeName("All");
                        break;
                    case R.id.rb_stock:
                        clickTypeName("Stock");
                        break;
                    case R.id.rb_mixed:
                        clickTypeName("Mixed");
                        break;
                    case R.id.rb_bond:
                        clickTypeName("Bond");
                        break;
                    case R.id.rb_currency:
                        clickTypeName("Currency");
                        break;
                    default:
                        break;
                }
            }
        });
        lv_fundinfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fund fund = null;
                if ("All".equals(typeName)) {
                    fund = fundList.get(position);
                } else {
                    fund = fundList2.get(position);
                }
                Intent intent = new Intent(MainActivity.this, FundInfoActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("fund", fund);
//                intent.putExtras(bundle);
                intent.putExtra("fund", fund);
                startActivity(intent);
            }
        });
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                btNetValue.setText("最新净值  ");
                ivNetValue.setImageResource(R.drawable.left2);
                netValueType = 0;
                ivRange.setImageResource(R.drawable.down);
                rangeType = 0;
                mSwipeLayout.setRefreshing(false);
                goPage(pageNo);
            }
        });
    }

    public void goPage(int pageno) {
        pbList.setVisibility(View.VISIBLE);
        //异步用http请求
        AsyncTask<String, Integer, HashMap<String, Object>> async = new AsyncTask<String, Integer, HashMap<String, Object>>() {
            @Override
            protected HashMap<String, Object> doInBackground(String... params) {
                HashMap<String, Object> map = null;
                String url = Constants.URL + "&pageno=" + params[0] + "&applyrecordno=" + Constants.APPLY_RECORD_NO;
                String html = Tools.getHTML(url);
                if (html.equals("")) {
                    return map;
                }
                map = new HashMap<String, Object>();
                JSONObject jo = null;
                String jsonStr = null;
                int pCount = 0;
                try {
                    jo = new JSONObject(html);
                    int totalrecords = Integer.parseInt(jo.getString("totalrecords"));
                    if ((totalrecords % Integer.parseInt(Constants.APPLY_RECORD_NO)) == 0) {
                        pCount = totalrecords / Integer.parseInt(Constants.APPLY_RECORD_NO);
                    } else {
                        pCount = (totalrecords / Integer.parseInt(Constants.APPLY_RECORD_NO)) + 1;
                    }
                    jsonStr = jo.getString("datatable");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                List<Fund> fundList = gson.fromJson(jsonStr, new TypeToken<List<Fund>>() {
                }.getType());
                map.put("pageno", params[0]);
                map.put("pageCount", pCount);
                map.put("fundlist", fundList);
                return map;
            }

            //doInBackground执行结束后
            @Override
            protected void onPostExecute(HashMap<String, Object> result) {
                pbList.setVisibility(View.GONE);
                if (result != null) {
                    pageNo = Integer.parseInt(result.get("pageno") + "");
                    pageCount = Integer.parseInt(result.get("pageCount") + "");
                    tvPage.setText(result.get("pageno") + "/" + result.get("pageCount"));
                    fundList = (ArrayList<Fund>) result.get("fundlist");
                    MyAdapter myAdapter = new MyAdapter(MainActivity.this, R.layout.my_base_item, fundList, netValueType);
                    lv_fundinfo.setAdapter(myAdapter);
                    btPrev.setEnabled(pageNo != 1);
                    btNext.setEnabled(pageNo != Integer.parseInt(result.get("pageCount") + ""));
                    clickTypeName(selectRadioBtn());
                } else {
                    Toast.makeText(MainActivity.this, "数据为空，请查看网络是否连接", Toast.LENGTH_LONG).show();
                }
            }
        };
        async.execute(pageno + "");
    }

    //最新净值/累计净值
    public void clickNet(View v) {
        if (netValueType == 1) {
            btNetValue.setText("最新净值  ");
            ivNetValue.setImageResource(R.drawable.left2);
            netValueType = 0;
        } else {
            btNetValue.setText("累计净值  ");
            ivNetValue.setImageResource(R.drawable.right2);
            netValueType = 1;
        }
        MyAdapter myAdapter = null;
        if ("All".equals(typeName)) {
            myAdapter = new MyAdapter(MainActivity.this, R.layout.my_base_item, fundList, netValueType);
        } else {
            fundList2 = Tools.findFundByTypeName(fundList, typeName);
            myAdapter = new MyAdapter(MainActivity.this, R.layout.my_base_item, fundList2, netValueType);
        }
        lv_fundinfo.setAdapter(myAdapter);
    }

    //    日涨跌幅,0为降序，1为升序
    public void clickRange(View v) {
        if (rangeType == 1) {
            ivRange.setImageResource(R.drawable.down);
            rangeType = 0;
        } else {
            ivRange.setImageResource(R.drawable.up);
            rangeType = 1;
        }
        MyAdapter myAdapter = null;
        fundList = Tools.findFundByRangeType(fundList, rangeType);
        if ("All".equals(typeName)) {
            myAdapter = new MyAdapter(MainActivity.this, R.layout.my_base_item, fundList, netValueType);
        } else {
            fundList2 = Tools.findFundByTypeName(fundList, typeName);
            fundList2 = Tools.findFundByRangeType(fundList2, rangeType);
            myAdapter = new MyAdapter(MainActivity.this, R.layout.my_base_item, fundList2, netValueType);
        }
        lv_fundinfo.setAdapter(myAdapter);
    }

    public void clickTypeName(String typeName) {
        this.typeName = typeName;
        fundList2 = fundList;
        if (!typeName.equals("All")) {
            fundList2 = Tools.findFundByTypeName(fundList2, typeName);
        }
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, R.layout.my_base_item, fundList2, netValueType);
        lv_fundinfo.setAdapter(myAdapter);
    }

    //上一页
    public void prevPage(View v) {
        goPage(pageNo - 1);
        clickTypeName(selectRadioBtn());
        btNetValue.setText("最新净值  ");
        ivNetValue.setImageResource(R.drawable.left2);
        netValueType = 0;
        ivRange.setImageResource(R.drawable.down);
        rangeType = 0;
    }

    //下一页
    public void nextPage(View v) {
        goPage(pageNo + 1);
        clickTypeName(selectRadioBtn());
        btNetValue.setText("最新净值  ");
        ivNetValue.setImageResource(R.drawable.left2);
        netValueType = 0;
        ivRange.setImageResource(R.drawable.down);
        rangeType = 0;
    }

    public String selectRadioBtn() {
        radioButton = (RadioButton) findViewById(rgFundType.getCheckedRadioButtonId());
        String rbText = radioButton.getText().toString();
        String selectName = null;
        switch (rbText) {
            case "全部":
                selectName = "All";
                break;
            case "股票":
                selectName = "Stock";
                break;
            case "混合":
                selectName = "Mixed";
                break;
            case "债券":
                selectName = "Bond";
                break;
            case "货币":
                selectName = "Currency";
                break;
        }
        return selectName;
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {// System.currentTimeMillis()无论何时调用，肯定大于2000
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}

