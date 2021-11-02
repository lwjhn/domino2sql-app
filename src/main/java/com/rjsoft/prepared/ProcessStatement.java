package com.rjsoft.prepared;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.domino2sql.config.ItemConfig;
import com.lwjhn.util.FileOperator;
import lotus.domino.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Vector;
import java.util.regex.Matcher;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class ProcessStatement implements com.lwjhn.domino2sql.ProcessStatement {
    protected Session session = null;
    protected String ftppath = null;
    protected DbConfig dbConfig = null;
    protected JSONObject extended_options = null;
    protected ItemConfig item_config_attachment = null;
    protected int index = -1;

    @Override
    public void action(PreparedStatement preparedStatement, Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception {
        initConfig(dbConfig, databaseCollection);
        if (item_config_attachment == null) return;
        preparedStatement.setObject(index, arcMssFiles(srcdoc, mssdbc).toJSONString(), item_config_attachment.getJdbc_type().getVendorTypeNumber(), item_config_attachment.getScale_length());
    }

    protected void initConfig(DbConfig dbConfig, DatabaseCollection databaseCollection) throws Exception {
        if (dbConfig == this.dbConfig) return;
        session = databaseCollection.getSession();
        index = dbConfig.getSql_field_others().length + 1;
        if ((ftppath = dbConfig.getFtppath()) == null)
            ftppath = DefaultConfig.FTPPATH;
        if ((extended_options = dbConfig.getExtended_options()) == null) return;
        item_config_attachment = extended_options.getObject("sql_field_attachment", ItemConfig.class);
    }

    @Override
    public void recycle() throws Exception {

    }

    protected String getQuery(Document doc) throws NotesException {
        String key, query = "!@Contains(Form;\"DelForm\") & Form != \"processing\" & DOCUNID = \"" + doc.getUniversalID() + "\"";
        if (!((key = doc.getItemValueString("UniAppUnid")) == null || "".equals(key)))
            query += " & DOCUNID = \"" + key + "\"";
        return query;
    }

    protected JSONObject arcMssFiles(Document doc, DatabaseCollection databaseCollection) throws Exception {
        String srv = null, dbpath = null, key = null, query = "";
        Database mssdb = null;
        DocumentCollection mssdc = null;
        Document mssdoc = null, msstdoc = null;
        JSONObject res = new JSONObject();
        try {
            if ((srv = doc.getItemValueString("MSSSERVER")) == null || "".equals(srv))
                srv = doc.getParentDatabase().getServer();
            if (srv == null || (dbpath = doc.getItemValueString("MSSDATABASE")) == null)
                return res;
            query = getQuery(doc);

            mssdb = databaseCollection.getDatabase(srv, dbpath);
            if (mssdb == null || !mssdb.isOpen())
                throw new Exception("can't open database ! " + srv + " !! " + dbpath);
            BaseUtils.recycle(mssdoc, mssdc);
            mssdc = mssdb.search(query, null, 0);
            mssdoc = mssdc.getFirstDocument();
            while (mssdoc != null) {
                arcMssDoc(mssdoc, res);
                mssdoc = mssdc.getNextDocument(msstdoc = mssdoc);
                BaseUtils.recycle(msstdoc);
            }
            return res;
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(mssdoc, mssdc);
        }
    }

    protected void arcMssDoc(Document mssdoc, JSONObject resObject) throws Exception {
        String key = null, dir = null, form = null;
        int i;
        EmbeddedObject eo = null;
        JSONArray res = null;
        JSONObject obj = null;
        Matcher matcher = null;
        try {
            if (mssdoc == null || (form = mssdoc.getItemValueString("form")) == null) return;
            form = form.toLowerCase();
            if (ftppath != null)
                new File(dir = FileOperator.getAvailablePath(
                        ftppath,
                        mssdoc.getParentDatabase().getServer().replaceAll("(/[^/]*)|([^/]*=)", ""),
                        mssdoc.getParentDatabase().getFilePath().replaceAll("[/\\\\.]", "-"),
                        (key = mssdoc.getItemValueString("DOCUNID")) != null ? key : mssdoc.getUniversalID(), mssdoc.getItemValueString("form")
                ).toLowerCase()).mkdirs();

            Vector<String> all = session.evaluate("@AttachmentNames", mssdoc);
            if (all == null || all.size() < 1) return;
            Vector<String> files = mssdoc.hasItem("AttachFile") ? mssdoc.getItemValue("AttachFile") : new Vector(),
                    vAlias = mssdoc.hasItem("AttachTitle") ? mssdoc.getItemValue("AttachTitle") : new Vector();

            if ((res = resObject.getJSONArray(form)) == null) resObject.put(form, res = new JSONArray());
            for (String file : all) {
                i = files.indexOf(file);
                obj = new JSONObject();
                //obj.put("unid", mssdoc.getUniversalID());
                obj.put("name", file);
                if (i < 0 || vAlias.size() <= i || "".equals(key = String.valueOf(vAlias.get(i)))) {
                    obj.put("alias", file);
                } else {
                    matcher = DefaultConfig.PATTERN_EXT.matcher(file);
                    obj.put("alias", key + (matcher.find() ? matcher.group() : ""));
                }
                if (dir != null) {
                    BaseUtils.recycle(eo);
                    if ((eo = mssdoc.getAttachment(file)) == null) continue;
                    eo.extractFile(key = dir + "/" + file);
                    obj.put("local", key);
                }
                res.add(obj);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(eo);
        }
    }
}
