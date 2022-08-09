package com.zcy.webexcel.Utils;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
public class DemoDataListener<T> implements ReadListener<T> {
    public static int BATCH_COUNT = 100;
    private List<T> cachedDataList;
    private final Consumer<List<T>> consumer;

    public DemoDataListener(Consumer<List<T>> consumer) {
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        this.consumer = consumer;
    }

    public void invoke(T data, AnalysisContext context) {
        this.cachedDataList.add(data);
        if (this.cachedDataList.size() >= BATCH_COUNT) {
            this.consumer.accept(this.cachedDataList);
            this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    private static final Logger log = LogManager.getLogger(DemoDataListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */

    /**
     * 缓存的数据
     */


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(this.cachedDataList)) {
            this.consumer.accept(this.cachedDataList);
        }
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        // 如果想转成成 Map<Integer,String>
        // 方案1： 不要implements ReadListener 而是 extends AnalysisEventListener
        // 方案2： 调用 ConverterUtils.convertToStringMap(headMap, context) 自动会转换
    }
    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        log.info("存储数据库成功！");
    }
}
