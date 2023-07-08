package com.dodal.meet.configuration;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class P6spyFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        Date currentDate = new Date();

        SimpleDateFormat sdt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        return new StringBuilder(sdt.format(currentDate)).append(" # Time : ").append(elapsed).append("ms\n").append(sql).toString();
    }

    private String formatSql(String category,String sql) {
        if(sql == null || sql.trim().equals("")) return sql;

        if (Category.STATEMENT.getName().equals(category)) {
            String tmpsql = sql.trim().toLowerCase(Locale.ROOT);
            if(tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            }else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            sql = "SQL LOG :"+ sql;
        }

        return sql;
    }
}
