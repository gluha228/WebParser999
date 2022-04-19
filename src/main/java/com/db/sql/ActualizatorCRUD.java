package com.db.sql;

import com.db.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ActualizatorCRUD {

    private final JdbcTemplate template;
    @Autowired
    public ActualizatorCRUD(MyDataSource dataSourse) {
        this.template = dataSourse.getTemplate();
    }

    public void createTable(){
        template.execute("CREATE TABLE IF NOT EXISTS ActualityInfo(lastUpdate varchar, url varchar)");
    }

    public void createActualityInfo(String category){
        template.update("INSERT INTO ActualityInfo VALUES(?, ?)", new Date(0), category);
    }


    public void updateActualityInfo(String category, Date update) {
        template.update("UPDATE ActualityInfo SET lastUpdate = ? WHERE url = ?", update, category);
    }

    public List<TableInfo> readActualityInfo() {
        return template.query("SELECT * from ActualityInfo", new BeanPropertyRowMapper<>(TableInfo.class));
    }

}
