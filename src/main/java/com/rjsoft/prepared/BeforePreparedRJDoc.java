package com.rjsoft.prepared;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino.DatabaseCollection;
import com.lwjhn.domino2sql.config.DbConfig;
import com.lwjhn.util.FileOperator;
import com.rjsoft.archive.RJUitilDSXml;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.RichTextItem;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */

public class BeforePreparedRJDoc extends BeforeArchive {
    private final String form = "processing", filename = "阅办单.html";

    public void action(Document srcdoc, DbConfig dbConfig, Connection connection, DatabaseCollection databaseCollection, DatabaseCollection mssdbc) throws Exception {
        super.action(srcdoc, dbConfig, connection, databaseCollection, mssdbc);
        //System.out.println("com.lwjhn.archive.prepared.BeforePreparedRJDoc.action:: unid->" + srcdoc.getUniversalID());
        String srv = null, dbpath = null, unid = srcdoc.getUniversalID();
        Database attachdb = null;
        File file;
        DocumentCollection mssdc = null;
        Document mssdoc = null;
        RichTextItem item = null;
        try {
            attachdb = mssdbc.getDatabase(
                    (srv = srcdoc.getItemValueString("MSSSERVER")) == null ? srcdoc.getParentDatabase().getServer() : srv,
                    dbpath = srcdoc.getItemValueString("MSSDATABASE"));
            if (attachdb == null || !attachdb.isOpen())
                throw new Exception("mssdatabase is nothing , or can't open . master docid " + unid);
            mssdc = attachdb.search("Form=\"" + form + "\" & DOCUNID = \"" + unid + "\"", null, 1);
            if (mssdc.getCount() == 1) {
                mssdoc = mssdc.getFirstDocument();
                if (!this.getVersion().equals(mssdoc.getItemValueString("$before_archive_version"))) {
                    mssdoc.remove(true);
                    BaseUtils.recycle(mssdoc, mssdc);
                    mssdoc = null;
                    mssdc = null;
                }
            }
            if (mssdoc != null) return;
            (file = new File(FileOperator.getAvailablePath(this.getFtppath(),
                    srv.replaceAll("(/[^/]*)|([^/]*=)", ""),
                    dbpath.replaceAll("[/\\\\.]", "-"),
                    unid, form).toLowerCase())).mkdirs();

            RJUitilDSXml.parseDSHtml(srcdoc, databaseCollection.getSession(), file = new File(file.getCanonicalPath() + "/" + form + unid + ".html"), Charset.forName("UTF-8"));
            if (!file.exists())
                throw new Exception("RJUitilDSXml.parseDSHtml error : create file error ! " + file.getName());

            mssdoc = mssdoc = this.createMssDoc(attachdb, form, file, unid, filename);
            mssdoc.replaceItemValue("$before_archive_version", this.getVersion());
            mssdoc.save(true);
            FileOperator.deleteDir(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            BaseUtils.recycle(item, mssdoc, mssdc);
        }
    }
}
