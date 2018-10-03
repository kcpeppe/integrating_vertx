package com.jclarity.safepoint.io;

import java.io.IOException;
import java.util.stream.Stream;

public interface DataSource {
    Stream<String> stream() throws IOException;
}
