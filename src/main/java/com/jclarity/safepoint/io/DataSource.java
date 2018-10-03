package com.jclarity.safepoint.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface DataSource<T> {

    Stream<T> stream() throws IOException;

}
