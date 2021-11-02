package com.rjsoft.prepared;

import lotus.domino.Document;
import lotus.domino.NotesException;

/**
 * @Author: lwjhn
 * @Date: 2021-1-21
 * @Description: com.rjsoft.prepared
 * @Version: 1.0
 */
public class ProcessStatementRJDoc extends ProcessStatement {
    @Override
    protected String getQuery(Document doc) throws NotesException {
        String key, query = "!@Contains(Form;\"DelForm\") & DOCUNID = \"" + doc.getUniversalID() + "\"";
        if (!((key = doc.getItemValueString("UniAppUnid")) == null || "".equals(key)))
            query += " & DOCUNID = \"" + key + "\"";
        return query;
    }
}
