package com.modak.modakapp.utils.weather;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class WeatherUtil {
    public void lookUpWeather() throws Exception {
        String serviceKey = "ZkbuXWyMg7WdOZEALBi7h1EF0fNOtK%2BADmdBCVogTspCJWyMXEI1YYxalb6MSnXFfxT5BKSPVy%2B7gIYsZwkB9A%3D%3D";
        int numOfRows = 100;
        int pageNo = 1;
        String baseDate = "20221003";    //조회하고싶은 날짜
        String baseTime = "1500";    //조회하고싶은 시간
        String dataType = "json";    //조회하고 싶은 type(json, xml 중 고름)
        String nx = "55";    //위도
        String ny = "127";    //경도

        String urlBuilder = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst"
                + "?serviceKey=" + serviceKey
                + "&numOfRows=" + numOfRows
                + "&pageNo=" + pageNo
                + "&dataType=" + dataType
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String result = sb.toString();
        System.out.println(result);

//=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//

        // response 키를 가지고 데이터를 파싱
//        JSONObject jsonObj_1 = new JSONObject(result);
//        String response = jsonObj_1.getString("response");
//
//        // response 로 부터 body 찾기
//        JSONObject jsonObj_2 = new JSONObject(response);
//        String body = jsonObj_2.getString("body");
//
//        // body 로 부터 items 찾기
//        JSONObject jsonObj_3 = new JSONObject(body);
//        String items = jsonObj_3.getString("items");
//        Log.i("ITEMS", items);
//
//        // items로 부터 itemlist 를 받기
//        JSONObject jsonObj_4 = new JSONObject(items);
//        JSONArray jsonArray = jsonObj_4.getJSONArray("item");
//
//        for (int i = 0; i < jsonArray.length(); i++) {
//            jsonObj_4 = jsonArray.getJSONObject(i);
//            String fcstValue = jsonObj_4.getString("fcstValue");
//            String category = jsonObj_4.getString("category");
//
//            if (category.equals("SKY")) {
//                weather = "현재 날씨는 ";
//                if (fcstValue.equals("1")) {
//                    weather += "맑은 상태로";
//                } else if (fcstValue.equals("2")) {
//                    weather += "비가 오는 상태로 ";
//                } else if (fcstValue.equals("3")) {
//                    weather += "구름이 많은 상태로 ";
//                } else if (fcstValue.equals("4")) {
//                    weather += "흐린 상태로 ";
//                }
//            }
//
//            if (category.equals("T3H") || category.equals("T1H")) {
//                tmperature = "기온은 " + fcstValue + "℃ 입니다.";
//            }
//
//            Log.i("WEATER_TAG", weather + tmperature);
//        }

    }

}