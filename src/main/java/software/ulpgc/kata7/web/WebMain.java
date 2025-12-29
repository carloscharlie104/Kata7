package software.ulpgc.kata7.web;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import software.ulpgc.kata7.app.DatabaseRecorder;
import software.ulpgc.kata7.app.DatabaseStore;
import software.ulpgc.kata7.app.MovieDeserializer;
import software.ulpgc.kata7.app.RemoteStore;
import software.ulpgc.kata7.io.Store;
import software.ulpgc.kata7.model.Movie;
import software.ulpgc.kata7.viewmodel.Histogram;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

public class WebMain {
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

            Javalin app = Javalin.create().start(7070);


            HistogramService histogramService = new HistogramService(store);
            HistogramAdapter adapter = new HistogramAdapter();
            HistogramController controller = new HistogramController(histogramService, adapter);
            // c
            app.get("/histogram", controller::getHistogram);
            app.get("/", ctx -> {
                // valores por defecto
                String field = "year";
                Integer from = 1900;
                Integer to = 2025;

                Histogram histogram = histogramService.buildHistogram(field, from, to);
                HistogramDto dto = adapter.adapt(histogram);

                ctx.json(dto);

            });

            System.out.println("Service running at http://localhost:7000");
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}