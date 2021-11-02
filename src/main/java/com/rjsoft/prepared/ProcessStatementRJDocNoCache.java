package com.rjsoft.prepared;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2json.Document2Json;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.domino2sql.config.DefaultConfig;
import com.lwjhn.util.FileOperator;
import com.rjsoft.archive.ExportOldFlow;
import com.rjsoft.archive.RJUitilDSXml;
import lotus.domino.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class ProcessStatementRJDocNoCache extends ProcessStatementRJDoc {
    @Override
    public void action(PreparedStatement preparedStatement, Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws NotesException, Exception {
        initConfig(dbConfig, databaseCollection);
        if (item_config_attachment == null) return;
        JSONObject response = arcMssFiles(srcdoc, mssdbc);
        if(response==null){
            response=new JSONObject();
        }
        handle(srcdoc, dbConfig, databaseCollection, mssdbc, response);
        preparedStatement.setObject(index, response.toJSONString(), item_config_attachment.getJdbc_type().getVendorTypeNumber(), item_config_attachment.getScale_length());
    }

    protected void handle(Document srcdoc, DbConfig dbConfig, DatabaseCollection databaseCollection, DatabaseCollection mssdbc, JSONObject response) throws Exception {
        String srv = null, dbpath = null, unid = null;
        try {
            System.out.println("com.lwjhn.archive.prepared.ProcessStatementRJDocNoCache.handle:: unid->" + srcdoc.getUniversalID());
            if ((ftppath = dbConfig.getFtppath()) == null)
                ftppath = DefaultConfig.FTPPATH;

            if ((srv = srcdoc.getItemValueString("MSSSERVER")) == null || "".equals(srv))
                srv = srcdoc.getParentDatabase().getServer();
            if (srv == null || (dbpath = srcdoc.getItemValueString("MSSDATABASE")) == null || "".equals(dbpath))
                throw new Exception("can not find item of mssdatabase from document . " + srcdoc.getUniversalID());

            String mssOpinion;
            if (this.extended_options.containsKey("export_opinion")){
                mssOpinion = srcdoc.getItemValueString("MSSOpinion");
                if (mssOpinion==null || "".equals(mssOpinion)){
                    mssOpinion = srcdoc.getItemValueString("OpinionlogDatabase");
                }
                if (mssOpinion==null || "".equals(mssOpinion)){
                    mssOpinion = srcdoc.getParentDatabase().getFilePath();
                }
                doc2json(mssdbc, srv,
                        mssOpinion,
                        unid = srcdoc.getUniversalID(),
                        "opinion", "Form=\"Opinion\" & PARENTUNID = \"" + unid + "\"",
                        "意见表.json", response);
            }

            if (this.extended_options.containsKey("export_flow")){
                mssOpinion = srcdoc.getItemValueString("MssFlow");
                if (mssOpinion==null || "".equals(mssOpinion)){
                    oldFlow(srcdoc, response);
                }else{
                    doc2json(mssdbc, srv,
                            srcdoc.getItemValueString("MssFlow"),
                            unid,
                            "flow", "Form=\"FlowForm\" & DOCUNID = \"" + unid + "\"",
                            "流程记录.json", response);
                }
            }
            if(this.extended_options.containsKey("export_processing")){
                final String form = "processing", filename = "阅办单.html";

                File file; String local;
                (file = new File(local = FileOperator.getAvailablePath(
                        ftppath,
                        srv.replaceAll("(/[^/]*)|([^/]*=)", ""),
                        dbpath.replaceAll("[/\\\\.]", "-"),
                        unid, form
                ).toLowerCase())).mkdirs();
                /*srcdoc=databaseCollection.getSession().getDatabase(srcdoc.getParentDatabase().getServer(),srcdoc.getParentDatabase().getFilePath())
                        .getDocumentByUNID(srcdoc.getUniversalID());*/
                srcdoc=databaseCollection.getDatabase(srcdoc.getParentDatabase().getServer(),srcdoc.getParentDatabase().getFilePath()).getDocumentByUNID(srcdoc.getUniversalID());
                System.out.println(srcdoc.getItemValueString("MssFlow"));
                System.out.println(srcdoc.getItemValueString("subject"));
                System.out.println(srcdoc.getItemValueString("DocWord"));
                RJUitilDSXml.parseDSHtml(srcdoc, databaseCollection.getSession(), file = new File(file.getCanonicalPath() + "/" + form + unid + ".html"), Charset.forName("UTF-8"));
                if (!file.exists())
                    throw new Exception("RJUitilDSXml.parseDSHtml error : create file error ! " + file.getName());
                putFile(file, response, filename, local, form);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected void oldFlow(Document srcdoc, JSONObject response) throws Exception {
        File file;
        JSONObject res = new JSONObject();
        String local;
        String form="flow";
        String filename="流程记录.html";
        try {
            String unid = srcdoc.getUniversalID();
            (file = new File(local = FileOperator.getAvailablePath(
                    ftppath,
                    srcdoc.getParentDatabase().getServer().replaceAll("(/[^/]*)|([^/]*=)", ""),
                    srcdoc.getParentDatabase().getFilePath().replaceAll("[/\\\\.]", "-"),
                    unid, form
            ).toLowerCase())).mkdirs();

            ExportOldFlow.toHtmlFile(srcdoc, file = new File(file.getCanonicalPath() + "/" + form + unid + ".html"));
            if (!file.exists()) throw new Exception("oldFlow error : create file error ! " + file.getName());

            putFile(file, response, filename, local, form);
        } catch (Exception e) {
            throw e;
        } finally {

        }
    }

    protected void doc2json(DatabaseCollection mssdbc, String srv, String dbpath, String unid, String form, String srcQuery, String filename, JSONObject response) throws Exception {
        String query = "";
        File file;
        Database mssdb = null;
        DocumentCollection mssdc = null;
        Document mssdoc = null;
        JSONObject res = new JSONObject();
        String local;
        try {
            if (srv == null || dbpath == null || unid == null || "".equals(dbpath) || "".equals(unid))
                return;
            mssdb = mssdbc.getDatabase(srv, dbpath);
            if (mssdb == null || !mssdb.isOpen())
                throw new Exception("can't open database ! " + srv + " !! " + dbpath);

            (file = new File(local = FileOperator.getAvailablePath(
                    ftppath,
                    srv.replaceAll("(/[^/]*)|([^/]*=)", ""),
                    dbpath.replaceAll("[/\\\\.]", "-"),
                    unid, form
            ).toLowerCase())).mkdirs();
            BaseUtils.recycle(mssdoc, mssdc);
            mssdc = mssdb.search(srcQuery, null, 0);

            Document2Json.toJSONFile(mssdc, file = new File(file.getCanonicalPath() + "/" + form + unid + ".json"));
            if (!file.exists()) throw new Exception("Document2Json error : create file error ! " + file.getName());

            putFile(file, response, filename, local, form);
        } catch (Exception e) {
            throw e;
        } finally {
            BaseUtils.recycle(mssdoc, mssdc);
        }
    }

    protected void putFile(File finalFile, JSONObject response, String filename, String local, String form) throws IOException {
        response.put(form, new JSONArray(){{
            add(new JSONObject() {{
                put("name", finalFile.getName());
                put("alias", filename);
                put("local", local==null ? finalFile.getCanonicalPath() : local + "/" + finalFile.getName());
            }});
        }});
    }
}
