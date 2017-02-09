package org.arquillian.cube.populator.nosql.api;

import org.arquillian.cube.populator.core.Populator;

public class NoSqlPopulator extends Populator<NoSqlPopulatorService, NoSqlPopulatorConfigurator> {

    public NoSqlPopulator(NoSqlPopulatorService populatorService) {
        super(populatorService);
    }

    @Override
    public NoSqlPopulatorConfigurator createExecutor() {
        return new NoSqlPopulatorConfigurator(this.host, this.bindPort, this.populatorService);
    }

}
