package com.rjsoft.ds;

//import java.io.Writer;

import lotus.domino.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * 初始化发文数据源的对象，实现DSProcess接口
 *
 * @author zhangguiping@rongji.com Created 2003-10-20
 */
public class DSProcessDispatch implements DSProcess {
    private Session session; // domino会话对象
    private Database db, ndb; // domino数据库对象，表示当前数据库
    private View viewOpinion, pview, pviewold; // 意见文档视图
    private Document document, ndoc; // 当前处理的主文档
    private DSItem dsItems[]; // 代表数据源中的具体域的对象列表

    /**
     * 缺省构造器
     *
     */
    public DSProcessDispatch() {

    }

    public void process(Vector dsItem, Document doc, Session session) throws Exception {
        db = ndb = null;
        viewOpinion = pview=pviewold=null;
        document= ndoc=null;
        dsItems = null;

        if(session==null) throw new Exception("session is null at DSProcessDispatch.process !");
        if (doc != null && session != null) {
            this.session = session;
            db = doc.getParentDatabase();
            ndb = session.getDatabase("", "names.nsf");
            pview = ndb.getView("PeopleByName");
            pviewold = ndb.getView("PeopleByNameold");
            document = doc;

            String mssServer = doc.getItemValueString("MSSServer");
            String mssOpinion = doc.getItemValueString("MSSOpinion");
            if ("".equals(mssOpinion))
                mssOpinion = doc.getItemValueString("OpinionlogDatabase");
            if (!"".equals(mssOpinion)) {
                viewOpinion = session.getDatabase(mssServer, mssOpinion).getView("Opinion");
            }

            if (viewOpinion == null) {
                viewOpinion = db.getView("Opinion");
            }
        }
        dsItems = new DSItem[dsItem.size()];
        int i = 0;
        for (Enumeration e = dsItem.elements(); e.hasMoreElements();) {
            DSItem item = (DSItem) e.nextElement();
            if (doc != null && session != null)
                dsItems[i++] = initValue(item);
            else
                dsItems[i++] = item;
        }
    }

    private DSItem initValue(DSItem item) {
        try {
            String rawVal = item.getValue(); // 域值计算公式
            String name = item.getName(); // 域名
            String oMap = item.getOpinionMap(); // 意见域标志
            if (oMap != null && oMap.length() > 0) // 如果是意见类型，那么返回意见内容
            {

                item.setValue(getOpinion(name, item.getFormat()));
                return item;
            }
            String function = item.getFunction();
            if (function == "getOpinionUser") {
                getOpinionUser(rawVal);
                return item;
            } else if (function == "getOpinionBody") {
                getOpinionBody(rawVal);
                return item;
            }

            Vector v = null;
            if (rawVal == null || rawVal.length() < 1)
                v = document.getItemValue(name); // �1������蛑得挥屑扑愎剑敲粗苯臃祷赜蛑�
            else
                v = session.evaluate(item.getValue(), document); // 如果域值有计算公式，那么处理计算公式并返回结果

            if (v.isEmpty())
                item.setValue(""); // 如果公式没有返回值，那么返回空串
            else
                item.setValue(getValueFromVector(v, item.getDelima())); // 否则处理公式返回值
//            // 判断是否有值的格式化定义，如果有，要格式化值
//            if (item.getFormat() != null) {
//                String formatClassName = item.getFormat();
//                if (formatClassName.length() > 0) {
//                    try {
//                        FormatBase format = (FormatBase) Class.forName(formatClassName).newInstance();
//                        item.setValue(format.format(item.getValue()));
//                    } catch (ClassNotFoundException cnfe) {
//                        System.out.println("Format Value Error:ClassNotFoundException:" + formatClassName);
//                    } catch (IllegalAccessException iae) {
//                        System.out.println("Format Value Error:IllegalAccessException:" + formatClassName);
//                    } catch (InstantiationException ie) {
//                        System.out.println("Format Value Error:InstantiationException:" + formatClassName);
//                    } catch (Exception e) {
//                        System.out.println("Format Value Error:" + e.getMessage() + "\nFormat Class Name:" + formatClassName);
//                    }
//                }
//            }
        } catch (Exception e) {
            System.err.println("initValue() Error in DSProcessDispatch.java:" + e.getMessage());
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 从Vector中解析内容，然后返回内容对应的字符串值
     * 如果有分隔符定义并且Vector内容不只一个，那么用分隔符分隔这些值，否则用缺省的<b>","</b>分隔
     *
     * @param v
     *            Vector
     * @param delima
     *            分隔符
     * @return
     */
    private String getValueFromVector(Vector v, String delima) {
        String tmpVal = "";
        if (v == null)
            return "";
        if (v.isEmpty())
            return "";
        if (v.size() == 1) {
            tmpVal = "" + v.firstElement();
            // if (tmpVal.lastIndexOf(".0")>=0)
            // tmpVal=tmpVal.substring(0,tmpVal.length()-2);
            return process4Xml(tmpVal);
        }
        String retStr = "";
        int vcount = v.size();
        int count = 1;
        for (Enumeration e = v.elements(); e.hasMoreElements();) {
            tmpVal = e.nextElement().toString();
            // if (tmpVal.lastIndexOf(".0")>=0)
            // tmpVal=tmpVal.substring(0,tmpVal.length()-2);
            if (count < vcount) {
                if (delima != null && delima.length() > 0)
                    retStr += tmpVal + delima;
                else
                    retStr += tmpVal + ",";
            } else
                retStr += tmpVal + "";
            count++;
        }
        return process4Xml(retStr);
    }

    /**
     * 根据传入的文档域名返回对应域值
     *
     * @param itmName
     *            域名
     * @return String 域值
     * @throws Exception
     */
    private String getItemValue(String itmName) throws Exception {
        String retVal = "";
        try {
            Item itm = document.getFirstItem(itmName);
            if (itm != null) {
                switch (itm.getType()) { // 判断notes域类型以便分别处理
                    case Item.DATETIMES: // 日期时间
                        retVal = itm.getDateTimeValue().toString();
                        break;
                    case Item.NUMBERS: // 数字
                        retVal = itm.getValueInteger() + "";
                        break;
                    case Item.RICHTEXT: // rtf域
                        retVal = process4Xml(itm.getValueString());
                        break;
                    default: // 缺省的其它域类型
                        String tmpVal = itm.getValueString();
                        // 如果有非法字符,要用cdata的方式显示
                        retVal = process4Xml(tmpVal);
                        break;
                }
            }
        } catch (NotesException e) {
            System.err.println("getItemValue(itmName) Error:" + e.text + e.id);
            return "";
        }
        char old = '\013'; // 替换特殊字符
        retVal = retVal.replace(old, ' ');
        return retVal;
    }

    private String getOpinion(String itemName, String format) throws Exception {
        ViewEntryCollection vec = viewOpinion.getAllEntriesByKey(document.getUniversalID());
        if (vec.getCount() < 1)	return "";

        StringBuffer sb = new StringBuffer("");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String tempopinion = "", sep = "", tempsort = "";
        Vector tempf;
        Vector vcIndex = document.getItemValue("C_UnitIndexAll");
        Vector vcUser = session.evaluate("@Name([Abbreviate];C_UserReader)",document);
        String userName = document.getItemValueString("UserName");
        ViewEntry entry = vec.getFirstEntry();
        Document opinionDoc = null;
        while (entry != null) {
            opinionDoc = entry.getDocument();
            if (opinionDoc.getItemValueString("OpinionField").equals(itemName) || opinionDoc.getItemValueString("OpinionType").equals(itemName)) {
                if(vcUser.indexOf(opinionDoc.getItemValueString("OpinionUser"))==-1 || vcIndex.indexOf(opinionDoc.getItemValueString("UnitIndex"))==-1 || userName.equals(opinionDoc.getItemValueString("OpinionUser")) || userName.equals("")){
                    if (format == null) {

                        tempopinion += (sep + "<opinion>\n");
                        String temp;
                        temp = opinionDoc.getItemValueString("OpinionUser");
                        ndoc = pview.getDocumentByKey(temp, true);
                        if (ndoc == null && pviewold != null) {
                            ndoc = pviewold.getDocumentByKey(temp, true);
                        }
                        if (ndoc == null) {
                            tempsort = tempsort + sep + "999999";
                            tempopinion = tempopinion + "<userNum>999999</userNum>";
                        } else {
                            tempf = session.evaluate("@right(\"000000\"+@text(NUM);6)", ndoc);
                            temp = (String) tempf.firstElement();
                            tempf = session.evaluate("@right(\"000000\"+@If(@Contains(@text(NUM);\".\");@ReplaceSubstring(@text(NUM);\".\";\"\");@text(NUM)+\"0\");6)", ndoc);

                            temp = (String) tempf.firstElement();
                            if ("".equals(temp))
                                tempopinion = tempopinion + "<userNum>999999</userNum>";
                            else
                                tempopinion = tempopinion + "<userNum>" + temp + "</userNum>";
                            tempsort = tempsort + sep + opinionDoc.getItemValueString("num");
                        }

                        tempopinion += "<body>" + process4Xml(opinionDoc.getItemValueString("OpinionBody")) + "</body>\n";
                        if (opinionDoc.hasItem("OpinionOpenType")) {
                            tempopinion += "<openType>" + opinionDoc.getItemValueString("OpinionOpenType") + "</openType>\n";
                            tempopinion += "<openBody>" + process4Xml(opinionDoc.getItemValueString("OpinionOpenBody")) + "</openBody>\n";
                        }
                        if (opinionDoc.hasItem("OpinionUserTitle"))
                            temp = opinionDoc.getItemValueString("OpinionUserTitle") + "/";
                        else
                            temp = opinionDoc.getItemValueString("OpinionUser") + "/";

                        tempopinion += "<opinionDeptTitle>" + opinionDoc.getItemValueString("OpinionDeptTitle") + "</opinionDeptTitle>";
                        tempopinion += "<groupByOpinionUserDept>" + opinionDoc.getItemValueString("GroupByOpinionUserDept") + "</groupByOpinionUserDept>";


                        tempopinion += "<user>" + temp.substring(0, temp.indexOf("/")) + "</user>";
                        tempopinion += "<agent>" + opinionDoc.getItemValueString("OpinionAgent") + "</agent>";
                        Vector vcDate = opinionDoc.getItemValueDateTimeArray("OpinionTime");
                        Date date = ((DateTime) vcDate.elementAt(0)).toJavaDate();
                        tempopinion += "<date>" + formatter.format(date) + "</date>";
                        tempopinion += "<unid>" + opinionDoc.getUniversalID() + "</unid>";
                        tempopinion += "</opinion>\n";

                        sep = "~~";

                    } else {
                        sb.append(process4Xml(opinionDoc.getItemValueString(format)));
                    }
                }
            }
            entry = vec.getNextEntry(entry);
        }

        String[] oplist = tempopinion.split("~~");
        Arrays.sort(oplist);
        for (int i = 0; i < oplist.length; i++) {
            sb.append(oplist[i]);
        }
        if(!vcIndex.isEmpty()) vcIndex.removeAllElements();

        return sb.toString();
    }

    private String getOpinionUser(String fields) throws Exception {
        return getOpinionAttribute(fields, "User");
    }

    private String getOpinionBody(String fields) throws Exception {
        return getOpinionAttribute(fields, "Body");
    }

    private String getOpinionAttribute(String fields, String type) throws Exception {

        ViewEntryCollection vec = viewOpinion.getAllEntriesByKey(document.getUniversalID());
        if (vec.getCount() < 1)
            return "";

        StringBuffer sb = new StringBuffer("");
        // SimpleDateFormat formatter = new
        // SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        String[] fieldArr = fields.split(":");
        int i = 0, len = fieldArr.length;
        String opinionField, opinionType;
        ViewEntry entry = vec.getFirstEntry();
        Document opinionDoc = null;
        while (entry != null) {
            opinionDoc = entry.getDocument();
            opinionType = opinionDoc.getItemValueString("OpinionType");
            opinionField = opinionDoc.getItemValueString("OpinionField");
            for (i = 0; i < len; i++) {
                if (!(opinionField.equalsIgnoreCase(fieldArr[i]) || opinionType.equalsIgnoreCase(fieldArr[i]))) {
                    continue;
                }
                sb.append(process4Xml(opinionDoc.getItemValueString(fieldArr[i] + type) + " "));
            }

            entry = vec.getNextEntry(entry);
        }

        return sb.toString();
    }
    public static final Pattern PATTERN_LT = Pattern.compile("^<");
    /**
     * 如果文本中有xml的实体字符，则要采用CDATA的方式输出
     *
     * @param in
     *            传入的文本
     * @return 输出的处理结果文本
     */
    private String process4Xml(String in) {
        if(in==null || "".equals(in)) return "";
        StringBuilder res = new StringBuilder("<![CDATA[");
        if(PATTERN_LT.matcher(in).lookingAt()) res.append((char)10);
        return res.append(in.replaceAll("]]>","]] >"))
                .append("]]>").toString();
        /*if (in == null || in.equals(""))
            return "";
        if (in.indexOf('>') >= 0 || in.indexOf('<') >= 0 || in.indexOf('"') >= 0 || in.indexOf('&') >= 0 || in.indexOf("'") >= 0) {
            char[] chars1 = new char[1];
            chars1[0] = 91; // [
            char[] chars2 = new char[1];
            chars2[0] = 93; // ]
            return "<!" + new String(chars1) + "CDATA" + new String(chars1) + " " + in + " " + new String(chars2) + new String(chars2) + ">";
        } else
            return in;*/
    }

    public void buildResultXml(java.io.PrintWriter pw) throws Exception {
        if (dsItems == null)
            throw new Exception("数据源项目不存在或者没有正确初始化！");
        boolean oMapFlag = false;
        pw.println("content-type:text/xml; charset=UTF-8");
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<items>");

        for (int i = 0; i < dsItems.length; i++) {
            if (dsItems[i].getOpinionMap() != null && dsItems[i].getOpinionMap().length() > 0) // 如果是意见域标志，那么输出意见域格式xml片断
            {
                pw.println("<item opinionmap=\"" + dsItems[i].getOpinionMap() + "\">");
                oMapFlag = true;
            } else
                pw.println("<item>");
            // 域名xml片断
            pw.println("<name>" + process4Xml(dsItems[i].getName() + "") + "</name>");
            // 域描述xml片断
            pw.println("<desc>" + process4Xml(dsItems[i].getDesc() + "") + "</desc>");
            // 域值xml片断
            if (dsItems[i].getValue().trim().length() > 0) { // 如果有域值
                if (dsItems[i].getDelima() != null)
                    pw.print("<value delima=\"" + dsItems[i].getDelima() + "\">");
                else
                    pw.print("<value>");
                pw.print(dsItems[i].getValue());
            } else { // 如果没有域值
                if (dsItems[i].getDelima() != null)
                    pw.print("<value delima=\"" + dsItems[i].getDelima() + "\">");
                else
                    pw.print("<value>");
            }
            pw.println("</value>");
            pw.println("</item>");
        }
        pw.println("</items>");
    }

    public String getResultXml() throws Exception {
        if (dsItems == null)
            throw new Exception("数据源项目不存在或者没有正确初始化！");
        boolean oMapFlag = false;
        String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        ret += "<items>";
        for (int i = 0; i < dsItems.length; i++) {
            // 如果是意见域标志，那么输出意见域格式xml片断
            if (dsItems[i].getOpinionMap() != null && dsItems[i].getOpinionMap().length() > 0) {
                ret += "<item opinionmap=\"" + dsItems[i].getOpinionMap() + "\">";
                oMapFlag = true;
            } else
                ret += "<item>";
            // 域名xml片断
            ret += "<name>" + process4Xml(dsItems[i].getName() + "") + "</name>";
            // 域描述xml片断
            ret += "<desc>" + process4Xml(dsItems[i].getDesc() + "") + "</desc>";
            // 域值xml片断
            if (dsItems[i].getValue().trim().length() > 0) { // 如果有域值
                if (dsItems[i].getDelima() != null)
                    ret += "<value delima=\"" + dsItems[i].getDelima() + "\">";
                else
                    ret += "<value>";
                ret += dsItems[i].getValue();
            } else { // 如果没有域值
                if (dsItems[i].getDelima() != null)
                    ret += "<value delima=\"" + dsItems[i].getDelima() + "\">";
                else
                    ret += "<value>";
            }
            ret += "</value>";
            ret += "</item>";
        }
        ret += "</items>";
        return ret;
    }

}