/*
 * Created on 2003-8-6
 */
package com.rjsoft.ds;
import lotus.domino.Document;
import lotus.domino.Session;

import java.util.Vector;

/**
 * 用于处理数据源的对象接口，所有具体的处理对象要实现本接口
 * @author zhangguiping@rongji.com
 */
public interface DSProcess {
    public void process(Vector dsItem,Document doc,Session session)throws Exception;
    //public void buildResultXmlbak(Writer w)throws Exception;
    public String getResultXml()throws Exception;
}
