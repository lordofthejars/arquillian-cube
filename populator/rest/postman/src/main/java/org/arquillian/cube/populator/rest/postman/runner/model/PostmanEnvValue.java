package org.arquillian.cube.populator.rest.postman.runner.model;

public class PostmanEnvValue {
    public String key;
    public String value;
    public String type;
    public String name;

    @Override
    public String toString() {
        return "["+key+":"+value+"]";
    }
}
