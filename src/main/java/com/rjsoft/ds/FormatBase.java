package com.rjsoft.ds;

/**
 * 用于格式化数据源的值输出格式的通用接口。 <br>
 * 所有需要格式化输出域值的对象必须实现此接口。
 *
 * @author zhangguiping@rongji.com Created 2004-1-8
 */
public interface FormatBase {
    /**
     * 格式化输出传入的初始值的内容
     *
     * @param raw
     *            输入的初始值
     * @return String：格式化输出结果
     */
    public String format(String raw);

    /**
     *
     * @param item
     *            输入的初始域对象
     * @return DSItem：其值被格式后的DSItem对象
     */
    public DSItem format(DSItem item);
}
