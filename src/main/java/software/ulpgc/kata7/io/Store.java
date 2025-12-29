package software.ulpgc.kata7.io;

import software.ulpgc.kata7.model.Movie;

import java.util.stream.Stream;

public interface Store {
    Stream<Movie> movies();
}
