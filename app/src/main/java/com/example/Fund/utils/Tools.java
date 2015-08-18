package com.example.Fund.utils;

import com.example.Fund.bean.Fund;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/7/20 0020.
 */
public class Tools {
    /**
     * 获取一个连接地址上的html
     * @param url 地址
     * @return
     */
    public static String getHTML(String url){
        HttpClient hc = new DefaultHttpClient();
        StringBuilder html = new StringBuilder();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = hc.execute(get);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){//返回成功
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    BufferedReader br  = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 80*1024);
                    String line = null;
                    while((line = br.readLine()) != null){
                        html.append(line);
                    }
                    br.close();
                }

            }else{
                System.out.println("取回失败，返回码为：" + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }finally{
            hc.getConnectionManager().shutdown();
        }

        return html.toString();
    }


    /**
     * 返回小数点后面保留位数
     * @param number 需要转换的数字
     * @param digit  小数点后几位
     * @return
     */
    public static String numberFormat(float number,int digit){
        StringBuilder formatStr = new StringBuilder("0.");
        for(int i=0;i < digit;i++){
            formatStr.append("0");
        }
        DecimalFormat df = new DecimalFormat(formatStr.toString());
        return df.format(number);
    }

    /**
     * 从所有基金中选出某种类型的基金
     * @param fundList 所有基金的信息
     * @param typename 基金类型
     * @return
     */
    public static ArrayList<Fund> findFundByTypeName(ArrayList<Fund> fundList,String typename){
        ArrayList<Fund> dataList = null;
        try{
            dataList = new ArrayList<Fund>();
            JSONObject jo = new JSONObject(Constants.TYPE_NAME);
            if(fundList != null){
                for (Fund fund : fundList) {
                    if(fund.fundinvestmenttypename.equals(jo.getString(typename))){
                        dataList.add(fund);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 根据涨跌来排序
     * @param fundList
     * @param RangeType 0为降序 1升序
     * @return
     */
    public static ArrayList<Fund> findFundByRangeType(ArrayList<Fund> fundList,int RangeType){
        if(fundList!=null){
            for (int i = 0; i < fundList.size()-1; i++) {
                for (int j = i + 1; j < fundList.size(); j++) {
                    if(RangeType == 1){
                        if(fundList.get(i).fundincreasepercent>fundList.get(j).fundincreasepercent){
                            fundList.set(i, fundList.get(j));
                            fundList.set(j, fundList.get(i));
                        }
                    }else{
                        if(fundList.get(i).fundincreasepercent<fundList.get(j).fundincreasepercent){
                            fundList.set(i, fundList.get(j));
                            fundList.set(j, fundList.get(i));
                        }
                    }
                }
            }
        }
        return fundList;
    }
}
