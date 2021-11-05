package pt.up.fe.els2021.model;

import java.util.List;

public record Table(List<String> columnNames, List<List<String>> columns) {
}
