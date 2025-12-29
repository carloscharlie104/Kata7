package software.ulpgc.kata7.app.db;

import software.ulpgc.kata7.app.DatabaseRecorder;
import software.ulpgc.kata7.app.DatabaseStore;
import software.ulpgc.kata7.app.MovieDeserializer;
import software.ulpgc.kata7.app.RemoteStore;
import software.ulpgc.kata7.app.Desktop;
import software.ulpgc.kata7.io.Store;
import software.ulpgc.kata7.model.Movie;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Main {
    private static final String DATABASE = "movies.db";

    public static void main(String[] args) throws SQLException {
        boolean created = !new File(DATABASE).exists();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE)) {
            connection.setAutoCommit(false);

            if (created) {
                System.out.println("Importing movies into Sqlite...");
                Stream<Movie> movies = new RemoteStore(MovieDeserializer::fromTsv).movies();
                new DatabaseRecorder(connection).record(movies);
                System.out.println("Movies imported!");
            }

            Store store = new DatabaseStore(connection);
            Desktop.with(store).display().setVisible(true);
        }
    }
}
