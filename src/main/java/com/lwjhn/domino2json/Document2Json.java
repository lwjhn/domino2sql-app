package com.lwjhn.domino2json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.util.FileOperator;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.RichTextItem;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @Author: lwjhn
 * @Date: 2020-11-25
 * @Description: com.lwjhn.domino2json
 * @Version: 1.0
 */
public class Document2Json {
    static public JSONArray toJSON(DocumentCollection dc) throws Exception {
        Document doc = null, tdoc = null;
        JSONArray res = new JSONArray();
        try {
            doc = dc.getFirstDocument();
            while (doc != null) {
                res.add(toJSON(doc));
                doc = dc.getNextDocument(tdoc = doc);
                BaseUtils.recycle(tdoc);
                tdoc = null;
            }
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(doc, tdoc);
        }
    }

    static public JSONObject toJSON(Document doc) throws Exception {
        Item item = null;
        JSONObject res = new JSONObject();
        try {
            for (Object o : doc.getItems()) {
                BaseUtils.recycle(item);
                item = (Item) o;
                if (!DefaultConfig.PATTERN_TABLE.matcher(item.getName()).matches()) continue;
                res.put(item.getName().toLowerCase(), item.getType() == Item.RICHTEXT ? ((RichTextItem) item).getUnformattedText() : (
                        item.getType() == Item.DATETIMES ? item.getText() : item.getValues()
                ));
            }
            res.put("$createtime", doc.getCreated().getLocalTime());
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(item);
        }
    }

    public static String toJSONString(Document doc) throws Exception {
        return toJSON(doc).toJSONString();
    }

    public static void toJSONFile(Document doc, String path) throws Exception {
        toJSONFile(doc, path, Charset.forName("UTF-8"));
    }

    public static void toJSONFile(Document doc, File path) throws Exception {
        toJSONFile(doc, path, Charset.forName("UTF-8"));
    }

    public static void toJSONFile(Document doc, String path, Charset charset) throws Exception {
        FileOperator.newFile(path, toJSONString(doc).getBytes(charset));
    }

    public static void toJSONFile(Document doc, File path, Charset charset) throws Exception {
        FileOperator.newFile(path, toJSONString(doc).getBytes(charset));
    }

    public static String toJSONString(DocumentCollection dc) throws Exception {
        return toJSON(dc).toJSONString();
    }

    public static void toJSONFile(DocumentCollection dc, String path) throws Exception {
        toJSONFile(dc, path, Charset.forName("UTF-8"));
    }

    public static void toJSONFile(DocumentCollection dc, File path) throws Exception {
        toJSONFile(dc, path, Charset.forName("UTF-8"));
    }

    public static void toJSONFile(DocumentCollection dc, String path, Charset charset) throws Exception {
        FileOperator.newFile(path, toJSONString(dc).getBytes(charset));
    }

    public static void toJSONFile(DocumentCollection dc, File path, Charset charset) throws Exception {
        FileOperator.newFile(path, toJSONString(dc).getBytes(charset));
    }
}
