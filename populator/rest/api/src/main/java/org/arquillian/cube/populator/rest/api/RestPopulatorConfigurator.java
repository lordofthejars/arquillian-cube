package org.arquillian.cube.populator.rest.api;

import org.arquillian.cube.populator.core.Populator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestPopulatorConfigurator implements Populator.PopulatorConfigurator {

    private String host;
    private int bindPort;
    private RestPopulatorService populatorService;
    private List<String> datasets = new ArrayList<>();

    private boolean urlOverride = true;

    RestPopulatorConfigurator(String host, int bindPort, RestPopulatorService populatorService) {
        this.host = host;
        this.bindPort = bindPort;
        this.populatorService = populatorService;
    }

    public RestPopulatorConfigurator avoidUrlOverride() {
        this.urlOverride = false;
        return this;
    }

    public RestPopulatorConfigurator usingDataSets(String... datasets) {
        this.datasets.addAll(Arrays.asList(datasets));
        return this;
    }

    @Override
    public void execute() {
        if (urlOverride) {
            this.populatorService.execute(this.host, this.bindPort, Collections.unmodifiableList(this.datasets));
        } else {
            this.populatorService.execute(Collections.unmodifiableList(this.datasets));
        }
    }

    @Override
    public void clean() {
        if (urlOverride) {
            this.populatorService.clean(this.host, this.bindPort, Collections.unmodifiableList(this.datasets));
        } else {
            this.populatorService.clean(Collections.unmodifiableList(this.datasets));
        }
    }
}
