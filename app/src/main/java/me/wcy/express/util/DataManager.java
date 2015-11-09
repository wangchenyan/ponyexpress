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
public class DataManager {
    private Context mContext;
    private DBHelper mDBHelper;
    private Dao<History, String> mHistoryDao;

    public static DataManager getInstance() {
        return SingletonHolder.instance;
    }

    public DataManager setContext(Context context) {
        mContext = context;
        mDBHelper = new DBHelper(mContext);
        try {
            mHistoryDao = mDBHelper.getDao(History.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    private static class SingletonHolder {
        private static DataManager instance = new DataManager();
    }

    private DataManager() {
    }

    public void updateHistory(ExpressInfo expressInfo) throws SQLException {
        History history;
        if (mHistoryDao.idExists(expressInfo.getPost_id())) {
            history = mHistoryDao.queryForId(expressInfo.getPost_id());
        } else {
            history = new History();
        }
        history.setPost_id(expressInfo.getPost_id());
        history.setCompany_param(expressInfo.getCompany_param());
        history.setCompany_name(expressInfo.getCompany_name());
        history.setCompany_icon(expressInfo.getCompany_icon());
        history.setIs_check(expressInfo.getIs_check());
        mHistoryDao.createOrUpdate(history);
    }

    public List<History> getHistoryList() throws SQLException {
        List<History> historyList = new ArrayList<>();
        for (History history : mHistoryDao) {
            historyList.add(0, history);
        }
        return historyList;
    }

    public List<History> getUnCheckList() throws SQLException {
        List<History> unCheckList = new ArrayList<>();
        for (History history : mHistoryDao) {
            if (!history.getIs_check().equals("1")) {
                unCheckList.add(0, history);
            }
        }
        return unCheckList;
    }

    public void deleteById(String id) throws SQLException {
        mHistoryDao.deleteById(id);
    }

}
