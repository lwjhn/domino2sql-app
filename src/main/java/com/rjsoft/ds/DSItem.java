/*
 * Created on 2003-8-6
 */
package com.rjsoft.ds;

/**
 * 数据源项对象，代表数据源中的一个具体域，一个数据源中包含许多数据源项
 * <br>以下简称为“域”或者“数据项”
 * <br>对象的属性的说明和配置请参考构造器中的说明
 * @author coca
 */
public class DSItem {
    private String name=null;           //域名称
    private String desc=null;           //域标题或者描述信息
    private String opinionMap=null;     //表示匹配的意见域按钮名称，在数据源配置xml文档的item节点中配置opinionmap属性
    private String value=null;          //域值
    private String delima=null;         //多值域的分隔符，在数据源配置xml文档的value节点中配置delima属性
    private String format=null;         //值的格式化对象名称，在数据源配置xml文档的value节点中配置format属性
    private String function=null;

    /**
     * 缺省构造器
     */
    public DSItem(){}
    /**
     * 构造器，接收域名、描述、域值、意见映射
     * @param n String  标示域名称，一般与文档中的域名称对应，比如：Subject，如果是意见，请填写意见对应的英文名称，比如：签发对应Sign
     * @param d String  域标题或者描述信息，比如：文件标题
     * @param v String  域值，配置注意事项如下：<br>
     * <ul>
     * <li>&lt;value&gt;&lt;/value&gt;：表示缺省采用域名称作为值的来源，实际处理数据源时系统自动到文档中取域名称对应的值</li>
     * <li>&lt;value&gt;<b>Domino公式</b>&lt;/value&gt;：表示缺省采用Domino公式计算结果作为值的来源，实际处理数据源时系统计算公式的结果</li>
     * </ul>
     * 数据源配置信息xml文件被预处理后返回的是配置的值或者公式
     * 返回的数据源被正式处理后值就是结算的结果
     * @param o String  表示匹配的意见域按钮名称<br>
     * 用于指示阅办单中填写意见标志的匹配以及指明本域为意见域（意见域需要特殊处理）,这是个可选属性,配置时值只能为空
     */
    public DSItem(String n,String d,String v,String o){
        this.name=n;
        this.desc=d;
        this.value =v;
        this.opinionMap =o;
    }

    /**
     * @return
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 在数据源配置xml文档的item节点中配置opinionmap属性，表示意见名称说明
     * @return
     */
    public String getOpinionMap() {
        return opinionMap;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * @param string
     */
    public void setDesc(String string) {
        desc = string;
    }

    /**
     * @param string
     */
    public void setOpinionMap(String string) {
        opinionMap = string;
    }

    /**
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

    /**
     * @param string
     */
    public void setValue(String string) {
        value = string;
    }

    /**
     * 返回多值域的分隔符，在数据源配置xml文档的value节点中配置delima属性
     * 可选属性
     * @return
     */
    public String getDelima() {
        return delima;
    }

    /**
     * @param string
     */
    public void setDelima(String string) {
        delima = string;
    }

    /**
     * 返回值的格式化对象名称，在数据源配置xml文档的value节点中配置format属性，比如com.rjsoft.oa.ds.ConvertToChineseDate
     * 可选属性
     * @return String
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param string
     */
    public void setFormat(String string) {
        format = string;
    }

    public String getFunction(){
        return function;
    }

    public void setFunction(String string){
        function = string;
    }
}
