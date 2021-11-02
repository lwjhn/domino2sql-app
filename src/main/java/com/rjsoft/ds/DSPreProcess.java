package com.rjsoft.ds;

import com.ibm.xml.parsers.SAXParser;
import org.xml.sax.*;

import java.io.InputStream;
import java.util.Vector;

/**
 * 用于预处理数据源的对象，其功能是读取数据源配置模板xml的信息，然后解析之，以便返回具体的数据源项和数据源项处理方法
 * @author coca
 */
public class DSPreProcess extends HandlerBase
{
    /**
     * 数据源中的每一项item的集合
     */
    private Vector allDS=new Vector();	//数据源中的域对象的集合
    /**
     * 数据源中定义的实现类的名称
     */
    private String imp ="";				//实现对象

    private boolean setName=false;		//设置数据源中name节点的标志
    private boolean setValue=false;		//设置数据源中value节点的标志
    private boolean setDesc=false;		//设置数据源中desc节点的标志
    private DSItem tmpDS=null;			//临时的数据源域对象
    /**
     * 构造器
     */
    public DSPreProcess(){}

    /**
     * 解析入口方法,接收xml文档流
     * @param is  InputStream：xml文档流
     */
    public void parse(InputStream is) throws Exception
    {
        InputSource isrc=new InputSource(is); //读取xml输入流
        SAXParser sp=new SAXParser();   //初始化解析器
        sp.setDocumentHandler(this);    //设置处理文档事件的对象，此处为对象本身
        sp.setErrorHandler(this);       //设置处理错误事件的对象，此处为对象本身
        try
        {
            sp.parse(isrc);   //开始解析
        }
        catch (Exception e)
        {
            System.err.println("DSPreProcess Error:"+e.getMessage());
            e.printStackTrace();
            throw new Exception("DSPreProcess错误：无法解析XML");
        }
        finally{
            is.close();
            isrc=null;
            is=null;
        }
    }
  /*
	public void parse(InputStream is) throws Exception
	{
		try
		{
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxFactory.newSAXParser();
			DefaultHandler defaultHandler = new DefaultHandler();
			//运用一个 InputStream 实例和一个SAX HandlerBase 实例
			saxParser.parse(is, defaultHandler);
		}
		catch (Exception e)
		{
			System.err.println("DSPreProcess Error:"+e.getMessage());
			e.printStackTrace();
			throw new Exception("DSPreProcess错误：无法解析XML");
		}
		finally
		{
			is.close();
			is=null;
		}
	}
	*/
    /**
     * 文档节点开始事件，在这里根据不同的节点名称设置各种标记和初始化类成员等
     */
    public void startElement(String name, AttributeList attrs){
        if (name.equalsIgnoreCase("items")){  //取得实现类的名称
            if (attrs!=null) this.imp=attrs.getValue("implement");
        }
        if (name.equalsIgnoreCase("item")){ //如果节点属性名称为item，那么
            if (tmpDS!=null) {  //如果有先前的数据源域对象，则追加这个对象到数据源域对象中
                DSItem ds=new DSItem();
                ds.setDesc(tmpDS.getDesc());
                ds.setName(tmpDS.getName());
                ds.setValue(tmpDS.getValue());
                ds.setOpinionMap(tmpDS.getOpinionMap());
                ds.setDelima(tmpDS.getDelima());
                ds.setFormat(tmpDS.getFormat());
                ds.setFunction(tmpDS.getFunction());
                allDS.addElement(ds);
            }
            tmpDS=new DSItem(); //然后新建一个临时数据源域对象
            if (attrs!=null) tmpDS.setOpinionMap(attrs.getValue("opinionmap"));  //设置意见映射属性
        }
        if (name.equalsIgnoreCase("name")){ //如果是name节点，则置name节点处理标志
            setName=true;
        }
        if (name.equalsIgnoreCase("desc")){ //如果是desc节点，则置desc节点处理标志
            setDesc=true;
        }
        if (name.equalsIgnoreCase("value")){ //如果是value节点，则置value节点处理标志
            setValue=true;
            //处理value节点的属性
            if (attrs!=null) {
                tmpDS.setDelima(attrs.getValue("delima"));
                tmpDS.setFormat(attrs.getValue("format"));
            } //如果有指定多值分隔符那么设置多值分隔符属性
        }
    }
    /**
     *文档节点结束事件，把最后一个item的节点的信息对应的DataSource增加到集合中
     */
    public void endElement(String name)
    {
        if (name.equalsIgnoreCase("items")) { //如果是items的结尾事件
            if (tmpDS!=null) {  //如果临时的数据源域文档不为空，那么把最后的这个数据源域追加到集合中
                DSItem ds=new DSItem();
                ds.setDesc(tmpDS.getDesc());
                ds.setName(tmpDS.getName());
                ds.setValue(tmpDS.getValue());
                ds.setOpinionMap(tmpDS.getOpinionMap());
                allDS.addElement(ds);
            }
        }
    }
    /**
     * 文档字符处理事件，在这里把数据源项的各种属性设置进去
     */
    public void characters(char ch[], int start, int length)
    {
        if (setName){ //如果是设置name标志,则把name的值写入临时数据源域
            tmpDS.setName(new String(ch,start,length).trim());
            setName=false;
        }
        if (setDesc){ //如果是设置desc标志,则把desc的值写入临时数据源域
            tmpDS.setDesc(new String(ch,start,length).trim());
            setDesc=false;
        }
        if (setValue){ //如果是设置value标志,则把value的值写入临时数据源域
            tmpDS.setValue(new String(ch,start,length).trim());
            setValue=false;
        }
    }
    /**
     * 返回数据源中所有的item对应的DataSource对象的集合
     * @return  Vector  DataSource的集合列表
     */
    public Vector getDataSourcese(){
        return this.allDS;
    }
    /**
     * 返回数据源的实现类名称
     * @return  String  数据源的实现类名称
     */
    public String getImplement(){
        return this.imp;
    }

    /**
     * 警告，用于处理出现的警告事件.
     */
    public void warning(SAXParseException ex)
    {
        System.err.println("[Warning] "+": "+ ex.getMessage());
    }

    /**
     * 错误，用于处理出现的错误事件.
     */
    public void error(SAXParseException ex)
    {
        System.err.println("[Error] "+ ": "+ex.getMessage());
    }

    /**
     * 严重错误，用于处理出现的严重错误事件.
     */
    public void fatalError(SAXParseException ex)
            throws SAXException
    {
        System.err.println("[Fatal Error] "+": "+ex.getMessage());
        throw ex;
    }
}
