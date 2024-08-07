package me.monst.pluginutil.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParameterizedQuery extends Query {
    
    private final List<Object> parameters = new LinkedList<>();
    
    ParameterizedQuery(String sql) {
        super(sql);
    }
    
    public ParameterizedQuery and(Object param) {
        parameters.add(param);
        return this;
    }
    
    public ParameterizedQuery and(Object... params) {
        parameters.addAll(Arrays.asList(params));
        return this;
    }
    
    public ParameterizedQuery and(Iterable<?> params) {
        params.forEach(parameters::add);
        return this;
    }
    
    @Override
    public boolean execute(Connection con) throws SQLException {
        try (PreparedStatement stmt = prepare(con)) {
            return stmt.execute();
        }
    }
    
    @Override
    public int executeUpdate(Connection con) throws SQLException {
        try (PreparedStatement stmt = prepare(con)) {
            return stmt.executeUpdate();
        }
    }
    
    @Override
    public ResultSet executeQuery(Connection con) throws SQLException {
        try (PreparedStatement stmt = prepare(con)) {
            return stmt.executeQuery();
        }
    }
    
    private PreparedStatement prepare(Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(sql);
        int i = 0;
        for (Object o : parameters) {
            stmt.setObject(++i, o);
        }
        return stmt;
    }

}
