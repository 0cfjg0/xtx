package com.itheima.xiaotuxian.constant.enums;

public enum TuxianIndexEnum {
    INDEX_KEYWORD("xtx_keyword", "template/xtx_keyword_mapping.json"),
    INDEX_GOODS("xtx_goods", "template/xtx_goods_mapping.json");


    private String indexName;
    private String mappingPath;

    TuxianIndexEnum(String indexName, String mappingPath) {
        this.indexName = indexName;
        this.mappingPath = mappingPath;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getMappingPath() {
        return mappingPath;
    }
}
