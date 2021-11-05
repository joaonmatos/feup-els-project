package pt.up.fe.els2021.interpreter;

import java.util.Map;

import pt.up.fe.els2021.model.Table;

public interface Command {
    void apply(Map<String, Table> programState);
}
