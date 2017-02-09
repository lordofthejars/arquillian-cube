package org.arquillian.cube.populator.nosql.api;

import org.arquillian.cube.populator.core.Populator;

import java.util.*;


public class NoSqlPopulatorConfigurator implements Populator.PopulatorConfigurator {

    private String host;
    private int bindPort;
    private NoSqlPopulatorService populatorService;
    private String database;
    private List<String> datasets = new ArrayList<>();
    private Map<String, Object> options = new HashMap<>();

    NoSqlPopulatorConfigurator(String host, int bindPort, NoSqlPopulatorService populatorService) {
        this.host = host;
        this.bindPort = bindPort;
        this.populatorService = populatorService;
    }

    public NoSqlPopulatorConfigurator withDatabase(String database) {
        this.database = database;
        return this;
    }

    public NoSqlPopulatorConfigurator usingDataSet(String dataset) {
        this.datasets.add(dataset);
        return this;
    }

    public NoSqlPopulatorConfigurator usingDataSets(String... datasets) {
        this.datasets.addAll(Arrays.asList(datasets));
        return this;
    }
    public NoSqlPopulatorConfigurator withOption(String key, String value, String... elements) {

        if (elements.length % 2 != 0) {
            throw new IllegalArgumentException("Extra options should be passed in form of (key, value)");
        }

        this.options.put(key, value);

        for (int i=0; i < elements.length; i+=2) {
            this.options.put(elements[i], elements[i+1]);
        }

        return this;
    }

    public NoSqlPopulatorConfigurator withOptions(Map<String, String> options) {
        this.options.putAll(options);
        return this;
    }

    @Override
    public void execute() {
        // TODO Improve this so connect and disconnect only happens once.
        // This implies for example observing @AfterClass to disconnect and add some boolean to know that connection is already started in execute and clean method.
        try {
            populatorService.connect(host, bindPort, this.database);
            populatorService.execute(Collections.unmodifiableList(datasets));
        } finally {
            populatorService.disconnect();
        }
    }

    public void clean() {
        try {
            populatorService.connect(host, bindPort, this.database);
            populatorService.clean();
        } catch (UnsupportedOperationException e) {
            //Nothing to do just log
        } finally {
            populatorService.disconnect();
        }
    }
}
