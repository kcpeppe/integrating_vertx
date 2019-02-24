package com.kodewerk.safepoint.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface DataSource<T> {

    T eosToken();

    Stream<T> stream() throws IOException;

}
