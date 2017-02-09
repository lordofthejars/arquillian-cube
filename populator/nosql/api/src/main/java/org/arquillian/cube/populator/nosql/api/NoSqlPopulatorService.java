package org.arquillian.cube.populator.nosql.api;

import org.arquillian.cube.populator.spi.PopulatorService;

import java.util.List;

public interface NoSqlPopulatorService<T> extends PopulatorService<T> {
    /**
     * Methods called to connect to the backend.
     * @param host to connect.
     * @param bindPort to connect.
     * @param database to use.
     */
    void connect(String host, int bindPort, String database);

    /**
     * Method called to disconnect from the backend.
     */
    void disconnect();

    /**
     * Method executed to populate model data.
     * @param resources used to populate. The meaning of this string depends on implementators. Some might treat this as directory, others like specific files (being in classpath or not), ...
     */
    void execute(List<String> resources);

    /**
     * Method executed to clean model data. Notice that this operation is not mandatory and Unsupported Operation Exception can be thrown.
     * @see UnsupportedOperationException which is called when backend does not implement clean operation.
     */
    void clean();
}
