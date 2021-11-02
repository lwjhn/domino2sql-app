package com.rjsoft.archive;

import com.lwjhn.util.FileOperator;
import lotus.domino.Document;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Vector;

public class ExportOldFlow {
    public static String getVector(Vector vector, int i) {
        return vector.size() > i ? (String) vector.get(i) : "";
    }

    public static String getCN(String name) {
        return name.replaceAll("(/[^/]*)|([^/]*=)", "");
    }

    public static String generate(Document doc) throws Exception {
        StringBuilder strFlowHead = new StringBuilder("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"></head><body>")
                .append("<table style=\"margin-left:30px;margin-right:30px;\"><tr><td style=\"color:#0000ff\">待办用户：")
                .append(doc.hasItem("C_UserReaderTitle") ? doc.getItemValueString("C_UserReaderTitle") : "")
                .append("</td></tr></table><br/><style>.flowTable thead td{padding:7px; background:#eee}.flowTable tbody td{padding:5px; border:1px dashed #fff; border-bottom:1px dashed #999;}</style>")
                .append("<TABLE class='flowTable' style='table-layout: fixed; border-collapse: collapse; width: 100%;'")
                .append("<colgroup><COL WIDTH=45><COL WIDTH=100><COL WIDTH=100><COL WIDTH=150><COL WIDTH=150><COL WIDTH=100%>");

        Vector U_UnitTerm = doc.hasItem("U_UnitTerm") ? doc.getItemValue("U_UnitTerm") : new Vector();
        Vector U_UnitEndTime = doc.hasItem("U_UnitEndTime") ? doc.getItemValue("U_UnitEndTime") : new Vector();
        boolean isTerm = U_UnitTerm.size() > 0 && U_UnitTerm.size() == U_UnitEndTime.size();
        strFlowHead.append(isTerm ? "<COL WIDTH=130>" : "")
                .append("<thead><tr><td>序号</td><td>环节名称</td><td>发送人</td><td>发送时间</td><td>流程动作</td><td>接收人</td>");
        if (isTerm) {
            strFlowHead.append("<td>办理期限</td>");
        }
        strFlowHead.append("</colgroup></tr></thead><tbody>");

        Vector U_UnitIndex = doc.hasItem("U_UnitIndex") ? doc.getItemValue("U_UnitIndex") : new Vector();
        Vector U_UnitToTitle = doc.hasItem("U_UnitToTitle") ? doc.getItemValue("U_UnitToTitle") : new Vector();
        Vector U_UnitUserTitle = doc.hasItem("U_UnitUserTitle") ? doc.getItemValue("U_UnitUserTitle") : new Vector();
        Vector U_UnitTo = doc.hasItem("U_UnitTo") ? doc.getItemValue("U_UnitTo") : new Vector();
        Vector U_UnitUser = doc.hasItem("U_UnitUser") ? doc.getItemValue("U_UnitUser") : new Vector();
        Vector U_UnitAgent = doc.hasItem("U_UnitAgent") ? doc.getItemValue("U_UnitAgent") : new Vector();
        Vector U_UnitAgentTitle = doc.hasItem("U_UnitAgentTitle") ? doc.getItemValue("U_UnitAgentTitle") : new Vector();

        Vector U_UnitName = doc.hasItem("U_UnitName") ? doc.getItemValue("U_UnitName") : new Vector();
        Vector U_UnitAction = doc.hasItem("U_UnitAction") ? doc.getItemValue("U_UnitAction") : new Vector();


        int index = 1;
        String temp, varTo, strFrom, time;

        StringBuilder flowBody = new StringBuilder();
        for (int i = 0; i < U_UnitIndex.size(); i++) {
            try {
                time = getVector(U_UnitEndTime, i);
                if ("".equals(time) || ":".equals(time))
                    continue;
                if (U_UnitToTitle.size() > 0) {
                    varTo = getVector(U_UnitToTitle, i).replaceAll("[;:]", "、");
                    strFrom = getVector(U_UnitUserTitle, i);
                    temp = getVector(U_UnitAgentTitle, i);
                    if (!"".equals(temp) && !":".equals(temp)) {
                        strFrom += temp;
                    }
                } else {
                    varTo = getCN(getVector(U_UnitTo, i)).replaceAll("[;:]", "、");
                    strFrom = getCN((String) U_UnitUserTitle.get(i));
                    temp = getVector(U_UnitAgent, i);
                    if (!"".equals(temp) && !":".equals(temp)) {
                        strFrom += getCN(temp);
                    }
                }
                StringBuilder flowRow = new StringBuilder("<tr><td style=\"BORDER-BOTTOM: #8bc5ff 1px dashed\"><font color=800000>")
                        .append(index++)
                        .append("</font></td>")
                        .append("<td>").append(getVector(U_UnitName, i)).append("</td>")
                        .append("<td><font color=000080>").append(strFrom).append("</font></td>")
                        .append("<td>").append(getVector(U_UnitEndTime, i)).append("</td>")
                        .append("<td>").append(getVector(U_UnitAction, i)).append("</td>")
                        .append("<td>").append(varTo).append("</td>");
                if (isTerm) {
                    flowRow.append("<td>").append(getVector(U_UnitTerm, i)).append("</td>");
                }
                flowRow.append("</tr>");
                flowBody.append(flowRow);
            } catch (Exception ignore) {
            }
        }

        String body = flowBody.toString();
        return "".equals(body) ? ""
                : strFlowHead.append(body).append("</tbody></TABLE></body></html>").toString();
    }

    public static void toHtmlFile(Document doc, String path) throws Exception {
        toHtmlFile(doc, path, Charset.forName("UTF-8"));
    }

    public static void toHtmlFile(Document doc, File path) throws Exception {
        toHtmlFile(doc, path, Charset.forName("UTF-8"));
    }

    public static void toHtmlFile(Document doc, String path, Charset charset) throws Exception {
        FileOperator.newFile(path, generate(doc).getBytes(charset));
    }

    public static void toHtmlFile(Document doc, File path, Charset charset) throws Exception {
        FileOperator.newFile(path, generate(doc).getBytes(charset));
    }
}
