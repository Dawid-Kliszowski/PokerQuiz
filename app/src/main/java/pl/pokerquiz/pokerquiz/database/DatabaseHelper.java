package pl.pokerquiz.pokerquiz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pl.pokerquiz.pokerquiz.BuildConfig;
import pl.pokerquiz.pokerquiz.R;
import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "poker_quiz.db";
    private static final int DATABASE_VERSION = 1;
    //private static final String TAG = "DatabaseHelper";

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private HashMap<Class, RuntimeExceptionDao<?, ?>> mClassRuntimeDaosMap;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, QuizQuestion.class);
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        mClassRuntimeDaosMap = new HashMap<>();

        mClassRuntimeDaosMap.put(Category.class, getRuntimeExceptionDao(Category.class));
        mClassRuntimeDaosMap.put(QuizQuestion.class, getRuntimeExceptionDao(QuizQuestion.class));
    }

    public <T> void insertIfNotExistsOrUpdate(T object, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(() -> {
            try {
                TransactionManager.callInTransaction(connectionSource, () -> {
                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());

                    runtimeDao.createOrUpdate(object);
                    return null;
                });
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.onDatabaseInserted();
            }
        });
    }

    public <T> void insertIfNotExistsOrUpdate(List<T> list, final OnDatabaseInsertedListener listener) {
        mExecutor.execute(() -> {
            try {
                TransactionManager.callInTransaction(connectionSource, () -> {
                    if (list.size() > 0) {
                        RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(list.get(0).getClass());

                        for (T object : list) {
                            runtimeDao.createOrUpdate(object);
                        }
                    }
                    return null;
                });
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.onDatabaseInserted();
            }
        });
    }

    public <T> List<T> getAll(Class clazz) {
        List<T> list;
        try {
            RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(clazz);

            list = runtimeDao.queryForAll();
        } catch (Exception e) {
            list = new ArrayList<>();
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public <T> List<T> getByField(Class clazz, String fieldName, Object value) {
        List<T> list;
        try {
            RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(clazz);

            list = runtimeDao.queryForEq(fieldName, value);
        } catch (Exception e) {
            list = new ArrayList<>();
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public <T> T getById(Class clazz, long id) {
        try {
            RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(clazz);
            T object = runtimeDao.queryForId(id);
            return object;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T> void delete(T object, OnDatabaseInsertedListener listener) {
        mExecutor.execute(() -> {
            try {
                TransactionManager.callInTransaction(connectionSource, () -> {
                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());
                    runtimeDao.delete(object);
                    return null;
                });
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.onDatabaseInserted();
            }
        });
    }

    public <T> void clearTable(T object, OnDatabaseInsertedListener listener) {
        mExecutor.execute(() -> {
            try {
                TransactionManager.callInTransaction(connectionSource, () -> {
                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());
                    runtimeDao.deleteBuilder().delete();
                    return null;
                });
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.onDatabaseInserted();
            }
        });
    }

    public <T> void deleteById(T object, long id, OnDatabaseInsertedListener listener) {
        mExecutor.execute(() -> {
            try {
                TransactionManager.callInTransaction(connectionSource, () -> {
                    RuntimeExceptionDao<T, Long> runtimeDao = (RuntimeExceptionDao<T, Long>) mClassRuntimeDaosMap.get(object.getClass());

                    runtimeDao.deleteById(id);
                    return null;
                });
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            if (listener != null) {
                listener.onDatabaseInserted();
            }
        });
    }

    public static interface OnDatabaseInsertedListener {
        public void onDatabaseInserted();
    }
}
