package com.rjsoft.archive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author: Wen KangFa
 * @Date: 2020-12-31
 * @Description: com.rjsoft.archive
 * @Version: 1.0
 */
public class FormatFlow {
    public static JSONArray jsonArraySort(JSONArray jsonArr, final String sortKey, final boolean is_desc) {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.size(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            private final String KEY_NAME = sortKey;

            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    valA = a.containsKey(KEY_NAME) ? a.getString(KEY_NAME) : "9999-99-99 00:00:00";
                    valB = b.containsKey(KEY_NAME) ? b.getString(KEY_NAME) : "9999-99-99 00:00:00";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (is_desc) {
                    return -valA.compareTo(valB);
                } else {
                    return -valB.compareTo(valA);
                }

            }
        });
        for (int i = 0; i < jsonArr.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * @Author
     *
     * {"unitname":"待办环节",
     * "unituser":"待办人",
     * "unitagent":"代理人",
     * "unittime":"操作时间",
     * "unitaction":"流程操作",
     * "unitnameto":"后续环节",
     * "unituserto":"后续环节办理人"
     * }
     *
     *
     */
    public static JSONArray formatFlowJson(String jsonString) {
        return formatFlowJson((JSONArray) JSON.parse(jsonString));
    }

    public static JSONArray formatFlowJson(JSONArray jArray) {
        JSONArray jsonSorted = FormatFlow.jsonArraySort(jArray, "unitendtime", false);
        JSONArray jsonRecordArray = new JSONArray();
        for (int i = 0; i < jsonSorted.size(); i++) {
            JSONObject jsonSingleRecord = jsonSorted.getJSONObject(i);
            if (checkUnitShow(jsonSingleRecord)) {//判断记录是否有效
                JSONObject jsonRecord = new JSONObject();
                if (jsonSingleRecord.containsKey("c_userdeptname"))
                    jsonRecord.put("c_userdeptname", getItemFirstValue(jsonSingleRecord, "c_userdeptname"));
                jsonRecord.put("unitstarttime", getItemFirstValue(jsonSingleRecord, jsonSingleRecord.containsKey("unitstarttime") ?  "unitstarttime" : "$createtime"));


                String sUnitName = getItemFirstValue(jsonSingleRecord, "c_unitname");  //jsonSingleRecord.containsKey("c_unitname") ? jsonSingleRecord.getJSONArray("c_unitname").getString(0) : "";
                String sUnitUser = getItemFirstValue(jsonSingleRecord, "c_username");  //jsonSingleRecord.containsKey("c_username") ? jsonSingleRecord.getJSONArray("c_username").getString(0) : "";
                String sUnitTime = getItemFirstValue(jsonSingleRecord, "unitendtime");  //jsonSingleRecord.containsKey("unitendtime") ? jsonSingleRecord.getJSONArray("unitendtime").getString(0) : "";
                String sUnitAction = getItemFirstValue(jsonSingleRecord, "unitaction");  //jsonSingleRecord.containsKey("unitaction") ? jsonSingleRecord.getJSONArray("unitaction").getString(0) : "";

                String sUnitHandler = getItemFirstValue(jsonSingleRecord, "unithandler");  //jsonSingleRecord.containsKey("unithandler") ? jsonSingleRecord.getJSONArray("unithandler").getString(0) : "";
                String sUnitAgentUser = getItemFirstValue(jsonSingleRecord, "c_agentuserid");  //jsonSingleRecord.containsKey("c_agentuserid") ? jsonSingleRecord.getJSONArray("c_agentuserid").getString(0) : "";

                //获取后续环节
                String sUnid = jsonSingleRecord.containsKey("unid") ? jsonSingleRecord.getJSONArray("unid").getString(0) : "";
                JSONArray jsonNextArray = getNextUnitInfo(sUnid, jsonSorted);
                if (jsonNextArray.size() == 0) {
                    //办理完毕节点
                    jsonRecord.put("unitname", sUnitName);
                    jsonRecord.put("unituser", sUnitUser);
                    jsonRecord.put("unittime", sUnitTime);
                    jsonRecord.put("unitaction", sUnitAction);
                    if (sUnitHandler.equals(sUnitAgentUser)) {
                        jsonRecord.put("unitagent", sUnitAgentUser);
                    }
                    jsonRecord.put("unitnameto", "");
                    jsonRecord.put("unituserto", "");
                    jsonRecordArray.add(jsonRecord);
                } else {
                    for (int j = 0; j < jsonNextArray.size(); j++) {
                        JSONObject oNextJson = jsonNextArray.getJSONObject(j);
                        jsonRecord.put("unitname", sUnitName);
                        jsonRecord.put("unituser", sUnitUser);
                        jsonRecord.put("unittime", sUnitTime);
                        jsonRecord.put("unitaction", sUnitAction);
                        if (sUnitHandler.equals(sUnitAgentUser)) {
                            jsonRecord.put("unitagent", sUnitAgentUser);
                        }

                        jsonRecord.put("unitnameto", oNextJson.getString("unitname"));
                        jsonRecord.put("unituserto", oNextJson.getString("unituser"));
                        jsonRecordArray.add(jsonRecord);
                    }
                }
            }
        }

        //处理多个环节汇聚到一个环节的情况，收回与撤办
        //System.out.println("FormatFlow>>>收回/撤办>>>>");
        JSONArray jsonFinallArray = new JSONArray();
        for (int i = 0; i < jsonRecordArray.size(); i++) {
            JSONObject jsonCur = jsonRecordArray.getJSONObject(i);
            for (int j = i + 1; j < jsonRecordArray.size(); j++) {
                //相邻后续环节中，是否有收回，撤办的情况
                //if (i+1 >= jsonRecordArray.size()) break;
                JSONObject jsonNext = jsonRecordArray.getJSONObject(j);
                if ("收回".equals(jsonCur.getString("unitaction")) || "撤办".equals(jsonCur.getString("unitaction"))) {
                    if (jsonCur.getString("unitname").equals(jsonNext.getString("unitname")) && jsonCur.getString("unitaction").equals(jsonNext.getString("unitaction")) && jsonCur.getString("unitnameto").equals(jsonNext.getString("unitnameto")) && jsonCur.getString("unituserto").equals(jsonNext.getString("unituserto"))) {
                        //当前环节，操作，后续环节，后续环节办理人一致，合并
                        String sUnitUser = jsonCur.getString("unituser");
                        sUnitUser = sUnitUser + "," + jsonNext.getString("unituser");
                        jsonCur.put("unituser", sUnitUser);
                        i = j;
                    } else {
                        break;
                    }
                }
            }
            jsonFinallArray.add(jsonCur);
        }
        return jsonFinallArray;
    }

    private static JSONArray getNextUnitInfo(String unid, JSONArray jsonSorted) {
        JSONArray ret = new JSONArray();
        boolean flag = false;
        //查找l_unituserunid和fromunituserunid等于unid的节点
        for (int i = 0; i < jsonSorted.size(); i++) {
            JSONObject jsonSingleRecord = jsonSorted.getJSONObject(i);
            String sLunituserunid = getItemFirstValue(jsonSingleRecord, "l_unituserunid");  //jsonSingleRecord.containsKey("l_unituserunid") ? jsonSingleRecord.getJSONArray("l_unituserunid").getString(0) : "";
            String sFunituserunid = getItemFirstValue(jsonSingleRecord, "fromunituserunid");  //jsonSingleRecord.containsKey("fromunituserunid") ? jsonSingleRecord.getJSONArray("fromunituserunid").getString(0) : "";
            flag = false;
            if ("".equals(sFunituserunid)) {
                if (unid.equals(sLunituserunid)) {
                    flag = true;
                }
            } else {
                if (unid.equals(sFunituserunid)) {
                    flag = true;
                }
            }

            if (flag) {
                String sUnitName = getItemFirstValue(jsonSingleRecord, "c_unitname");  //jsonSingleRecord.containsKey("c_unitname") ? jsonSingleRecord.getJSONArray("c_unitname").getString(0) : "";
                JSONObject jsonNextUnit = null;
                for (int j = 0; j < ret.size(); j++) {
                    JSONObject retJson = ret.getJSONObject(j);
                    if (sUnitName.equals(retJson.getString("unitname"))) {
                        jsonNextUnit = retJson;
                        break;
                    }
                }
                if (jsonNextUnit == null) {
                    jsonNextUnit = new JSONObject();
                    jsonNextUnit.put("unitname", sUnitName);
                    String sUnitUser = getItemFirstValue(jsonSingleRecord, "c_username");  //jsonSingleRecord.containsKey("c_username") ? jsonSingleRecord.getJSONArray("c_username").getString(0) : "";
                    jsonNextUnit.put("unituser", sUnitUser);
                    ret.add(jsonNextUnit);
                } else {
                    String sUnitUser = jsonNextUnit.getString("unituser");
                    sUnitUser = sUnitUser + "," + getItemFirstValue(jsonSingleRecord, "c_username");  //(jsonSingleRecord.containsKey("c_username") ? jsonSingleRecord.getJSONArray("c_username").getString(0) : "");
                    jsonNextUnit.put("unituser", sUnitUser);
                }

            }
        }
        return ret;
    }

    public static String getItemFirstValue(JSONObject jsonSingleRecord, String name) {
        Object item = jsonSingleRecord.get(name);
        item = item == null ? "" : (
                item instanceof List ? (((List) item).size() > 0 ? ((List) item).get(0) : "") : item
        );
        return String.valueOf(item instanceof JSONObject && ((JSONObject) item).containsKey("localTime")
                ? ((String)((JSONObject) item).get("localTime"))    //.replaceAll("(?i)\\s*(ze|gmt)\\d*\\s*","")
                : item);
    }

    private static boolean checkUnitShow(JSONObject jsonSingleRecord) {
        boolean ret = false;
        String sEndTime = getItemFirstValue(jsonSingleRecord, "unitendtime");  //jsonSingleRecord.containsKey("unitendtime") ? jsonSingleRecord.getJSONArray("unitendtime").getString(0) : "";
        String sUnitShow = getItemFirstValue(jsonSingleRecord, "unitshow");  //jsonSingleRecord.containsKey("unitshow") ? jsonSingleRecord.getJSONArray("unitshow").getString(0) : "";
        String sUserId = getItemFirstValue(jsonSingleRecord, "c_userid");  //jsonSingleRecord.containsKey("c_userid") ? jsonSingleRecord.getJSONArray("c_userid").getString(0) : "";
        String sAgentId = getItemFirstValue(jsonSingleRecord, "c_agentuserid");  //jsonSingleRecord.containsKey("c_agentuserid") ? jsonSingleRecord.getJSONArray("c_agentuserid").getString(0) : "";
        String sUnitHandler = getItemFirstValue(jsonSingleRecord, "unithandler");  //jsonSingleRecord.containsKey("unithandler") ? jsonSingleRecord.getJSONArray("unithandler").getString(0) : "";
        String sUnitAction = getItemFirstValue(jsonSingleRecord, "unitaction");  //.containsKey("unitaction") ? jsonSingleRecord.getJSONArray("unitaction").getString(0) : "";

        if (!"".equals(sEndTime) && !"0".equals(sUnitShow) && (sUserId.equals(sUnitHandler) || sAgentId.equals(sUnitHandler) || "收回".equals(sUnitAction) || "撤办".equals(sUnitAction) || "干预".equals(sUnitAction))) {
            //System.out.println("FormatFlow>>>checkUnitShow>>>>>>:" + (jsonSingleRecord.containsKey("c_unitname") ? jsonSingleRecord.getJSONArray("c_unitname").getString(0) : ""));
            ret = true;
        }
        return ret;
    }
}
