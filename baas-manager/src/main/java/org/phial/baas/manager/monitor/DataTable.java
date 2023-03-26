package org.phial.baas.manager.monitor;

import java.util.function.Consumer;

/**
 * Data table
 */
public class DataTable {

    public static final int DEFAULT_PADDING = 4;

    public static final String COLUMN_SEPARATOR = "| ";

    private int columnMaxLength[];
    private Object [][] data;

    private int padding = 4;

    /**
     * If the first line contains head fields
     */
    private boolean headerLinePresent;

    public DataTable(int[] columnMaxLength, Object[][] data) {
        this(columnMaxLength, data, DEFAULT_PADDING, false);
    }

    public DataTable(int[] columnMaxLength, Object[][] data, boolean headerLinePresent) {
        this(columnMaxLength, data, DEFAULT_PADDING, headerLinePresent);
    }

    public DataTable(Object[][] data) {
        this(null, data);
    }

    public DataTable(Object[][] data, boolean headerLinePresent) {
        this(null, data, DEFAULT_PADDING, headerLinePresent);
    }

    public DataTable(int[] columnMaxLength, Object[][] data, int padding, boolean headerLinePresent) {
        this.columnMaxLength = columnMaxLength;
        this.data = data;
        this.padding = padding;
        this.headerLinePresent = headerLinePresent;
        verify();
    }

    private void verify() {
        if (data == null || data.length == 0) throw new IllegalArgumentException("Data can not be empty");

        if (padding < 0) padding = 4;

        if (columnMaxLength == null) {
            int maxCol = data[0].length;
            for (int i = 1; i < data.length; i++) {
                if (data[i].length > maxCol) maxCol = data[i].length;
            }
            columnMaxLength = new int[maxCol];

            // extend arr
            for (int i = 0; i < data.length; i++) {
                Object d[] = data[i];
                for (int j = 0; j < d.length; j++) {
                    columnMaxLength[j] = maxLength(columnMaxLength[j], data[i][j]);
                }
            }
        }
    }

    private int maxLength(int currentLen, Object o) {
        String s = o.toString();
        int len = s.length();
        if (len > currentLen) return len;
        return currentLen;
    }

    public void format(Consumer<String> lineConsumer) {
        for (int i = 0; i < data.length; i++) {
            lineConsumer.accept(formatRow(i));
        }
    }

    public void output(StringBuffer out) {
        String separateLine = separateLine();
        if (separateLine != null) body(out, separateLine);
        body(out, formatRow(0));
        if (separateLine != null) body(out, separateLine);
        for (int i = 1; i < data.length; i++) {
            body(out, formatRow(i));
        }
        if (separateLine != null) body(out, separateLine);
    }

    private String separateLine() {
        if (isHeaderLinePresent()) {
            StringBuffer line = new StringBuffer();
            for (int col = 0; col < columnMaxLength.length; col++) {
                int len = columnMaxLength[col];
                int ll = len + padding + COLUMN_SEPARATOR.length();
                for (int i = 0; i < ll; i++) {
                    if (i == 0) {
                        line.append('+');
                    } else {
                        line.append('-');
                    }
                }
                if (col == columnMaxLength.length - 1) line.append('+');
            }
            return line.toString();
        }
        return null;
    }

    private String formatRow(int row) {
        Object col[] = data[row];
        StringBuffer line = new StringBuffer();
        for (int i = 0; i < col.length; i++) {
            String s = String.format("%-" + (columnMaxLength[i] + padding) + "s", col[i]);
            line.append(COLUMN_SEPARATOR);
            line.append(s);
            if (i == col.length - 1) {
                line.append(COLUMN_SEPARATOR);
            }
        }
        return line.toString();
    }

    private void body(StringBuffer sb, String content) {
        sb.append(content).append("\r\n");
    }

    public int[] getColumnMaxLength() {
        return columnMaxLength;
    }

    public Object[][] getData() {
        return data;
    }

    public void setColumnMaxLength(int[] columnMaxLength) {
        this.columnMaxLength = columnMaxLength;
    }

    public int rows() {
        return data.length;
    }

    public int columns() {
        if (rows() == 0) return -1;
        return data[0].length;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public boolean isHeaderLinePresent() {
        return headerLinePresent;
    }

    public void setHeaderLinePresent(boolean headerLinePresent) {
        this.headerLinePresent = headerLinePresent;
    }
}
