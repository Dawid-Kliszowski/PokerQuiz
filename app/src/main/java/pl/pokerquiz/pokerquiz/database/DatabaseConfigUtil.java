package pl.pokerquiz.pokerquiz.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import pl.pokerquiz.pokerquiz.datamodel.rest.Category;
import pl.pokerquiz.pokerquiz.datamodel.rest.QuizQuestion;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            Category.class,
            QuizQuestion.class
    };

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile(new File("app/src/main/res/raw/ormlite_config.txt"), classes);
    }
}
