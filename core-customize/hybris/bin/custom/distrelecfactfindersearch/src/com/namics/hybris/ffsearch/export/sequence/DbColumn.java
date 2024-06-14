package com.namics.hybris.ffsearch.export.sequence;

public class DbColumn {

    private final String columnName;
    private final String baseColumnName;
    private final boolean localized;
    private final boolean encodeFF;
    private final boolean encodeUrl;

    public DbColumn(final String columnName, final String baseColumnName, final boolean localized, final boolean encodeFF,
            final boolean encodeUrl) {
        this.columnName = columnName;
        this.baseColumnName = baseColumnName;
        this.localized = localized;
        this.encodeFF = encodeFF;
        this.encodeUrl = encodeUrl;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getBaseColumnName() {
        return baseColumnName;
    }

    public boolean isLocalized() {
        return localized;
    }

    public boolean isEncodeFF() {
        return encodeFF;
    }

    public boolean isEncodeUrl() {
        return encodeUrl;
    }
}
