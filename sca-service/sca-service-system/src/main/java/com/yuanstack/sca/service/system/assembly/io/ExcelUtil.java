package com.yuanstack.sca.service.system.assembly.io;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @description: 工具类，用于文件上传、下载、解析
 * @author: hansiyuan
 * @date: 2022/6/29 1:59 PM
 */
@Slf4j
public class ExcelUtil {
    /**
     * @param filePath                文件地址
     * @param clz                     实体类
     * @param list                    写入数据
     * @param includeColumnFiledNames 忽略的字段
     */
    public static void exportToExcel(String filePath, Class clz, List<?> list, Set<String> includeColumnFiledNames) {

        String fileName = "/Users/hansiyuan/excelDemo" + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";

        // 根据用户传入字段 假设我们要忽略 date
        //Set<String> includeColumnFiledNames = new HashSet<String>();
        // includeColumnFiledNames.add("date");
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName, clz).includeColumnFiledNames(includeColumnFiledNames).sheet("模板")
                .doWrite(list);

    }

    public static void noModelWrite(String filePath, List<List<String>> head, List<List<Object>> data) {
        long startTime = System.currentTimeMillis();
        EasyExcel.write(filePath).head(head).sheet("测试").doWrite(data);
        long endTime = (System.currentTimeMillis() - startTime) / 1000;
        LogUtils.info(log, ModelEnum.COMMON_UTILS, "time", endTime);
    }

    public static boolean noModelWrite(String filePath, List<String> excelHead, JSONArray jsonArray) {
        return noModelWrite(filePath, excelHead, jsonArray, null);
    }

    public static boolean noModelWrite(String filePath, List<String> excelHead, JSONArray jsonArray, String sheetName) {
        if (CollectionUtils.isEmpty(excelHead) || jsonArray.isEmpty()) {
            return false;
        }
        try {
            List<List<Object>> lists = dataList(jsonArray, excelHead);
            if (CollectionUtils.isNotEmpty(lists)) {
                long startTime = System.currentTimeMillis();
                ensureDirOK(filePath);
                EasyExcel.write(filePath).head(head(excelHead))
                        .sheet("用户信息")
                        .registerWriteHandler(ExcelUtil.getStyleStrategy())
                        .doWrite(lists);
                long endTime = (System.currentTimeMillis() - startTime);
                LogUtils.info(log, ModelEnum.COMMON_UTILS, "导出用时时间", endTime);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "导出失败", e);
        }
        return false;
    }

    public static void ensureDirOK(final String filePath) {
        File file = new File(filePath);
        String dirName = file.getParent();
        if (dirName != null) {
            File f = new File(dirName);
            if (!f.exists()) {
                boolean result = f.mkdirs();
                log.info(dirName + " mkdir " + (result ? "OK" : "Failed"));
            }
        }
    }

    /**
     * 构建列头
     * 里面包的list代表占几行
     *
     * @return
     */
    private static List<List<String>> head(List<String> fields) {
        List<List<String>> list = new ArrayList<List<String>>();
        fields.forEach(field -> {
            List<String> head = new ArrayList<String>();
            head.add(field);
            list.add(head);
        });
        return list;
    }

    /**
     * 构建数据
     */
    private static List<List<Object>> dataList(JSONArray jsonArray, List<String> fields) {
        List<List<Object>> list = new ArrayList<List<Object>>();
        if (jsonArray.isEmpty()) {
            return list;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            List<Object> data = new ArrayList<Object>();
            fields.forEach(field -> {
                Object o = jsonObject.get(field);
                LogUtils.info(log, ModelEnum.COMMON_UTILS, "jsonObject.get(field)", jsonObject.get(field));
                data.add(o == null ? "" : o);
            });
            list.add(data);
        }
        return list;
    }

    public static void main(String[] args) {

        List<String> strings = new ArrayList<>();
        strings.add("name");
        strings.add("age");
        strings.add("genger");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < 500000; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "li" + i);
            jsonObject.put("age", i);
            jsonObject.put("genger", i % 2);
            jsonArray.add(jsonObject);
        }
        String userHome = System.getProperty("user.home");
        noModelWrite(userHome + "/excelDemo/" + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx", strings, jsonArray, null);

    }

    public static HorizontalCellStyleStrategy getStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为灰色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        // 字体样式
        headWriteFont.setFontName("ubuntu");
        // headWriteFont.setFontName("Frozen");
        headWriteCellStyle.setWriteFont(headWriteFont);
        //自动换行
        headWriteCellStyle.setWrapped(false);
        // 水平对齐方式
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
//        contentWriteCellStyle.setFillPatternType(FillPatternType.SQUARES);
        // 背景白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        // contentWriteFont.setFontHeightInPoints((short) 12);
        // 字体样式
        // headWriteFont.setFontName("Frozen");
        contentWriteFont.setFontName("ubuntu");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    /**
     * 获取所有对象属性名称
     **/
    public static String[] getFiledNames(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }
}
