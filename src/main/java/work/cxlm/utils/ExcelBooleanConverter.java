package work.cxlm.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * created: 2022/11/5 02:57
 *
 * @author Chiru
 */
public class ExcelBooleanConverter implements Converter<Boolean> {

    private static final String YES = "是";
    private static final String NO = "否";

    @Override
    public Class<Boolean> supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Boolean convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String value = cellData.getStringValue();
        return YES.equals(value);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Boolean value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        // 添加字符串: "ms"
        return new WriteCellData<>(null != value && value ? YES : NO);
    }
}
