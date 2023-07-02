package datart.provider;

import datart.Column;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class TableInfo {

    private String tableName;

    private List<String> primaryKeys;

    private Set<Column> columns;

}
