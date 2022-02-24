package org.customauth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.security.Key;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Callback implements Serializable {

    private String type;

    private List<KeyValue> output;

    private List<KeyValue> input;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<KeyValue> getOutput() {
        return output;
    }

    public void setOutput(List<KeyValue> output) {
        this.output = output;
    }

    public List<KeyValue> getInput() {
        return input;
    }

    public void setInput(List<KeyValue> input) {
        this.input = input;
    }
}
