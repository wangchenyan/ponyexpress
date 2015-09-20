/**
 * 2015-4-2
 */
package me.wcy.express.util;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.wcy.express.database.DBHelper;
import me.wcy.express.database.History;
import me.wcy.express.model.ExpressInfo;

/**
 * @author wcy
 */
public class StorageManager {
    private DBHelper dbHelper;
    private Dao<History, String> historyDao;

    public StorageManager(Context context) {
        super();
        dbHelper = new DBHelper(context);
    }

    public void storeData(ExpressInfo expressInfo) throws SQLException {
        historyDao = dbHelper.getDao(History.class);
        History history = new History();
        history.setPost_id(expressInfo.getPost_id());
        history.setCompany_param(expressInfo.getCompany_param());
        history.setCompany_name(expressInfo.getCompany_name());
        history.setCompany_icon(expressInfo.getCompany_icon());
        history.setIs_check(expressInfo.getIs_check());
        historyDao.createOrUpdate(history);
    }

    public List<History> getHistoryList() throws SQLException {
        historyDao = dbHelper.getDao(History.class);
        List<History> historyList = new ArrayList<>();
        for (History history : historyDao) {
            historyList.add(0, history);
        }
        return historyList;
    }

    public List<History> getUnCheckList() throws SQLException {
        historyDao = dbHelper.getDao(History.class);
        List<History> unCheckList = new ArrayList<>();
        for (History history : historyDao) {
            if (!history.getIs_check().equals("1")) {
                unCheckList.add(0, history);
            }
        }
        return unCheckList;
    }

    public void deleteById(String id) throws SQLException {
        historyDao.deleteById(id);
    }
}
