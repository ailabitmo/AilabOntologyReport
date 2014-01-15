package ru.ifmo.ailab.jaspersparql;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.core.Var;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.base.JRBaseField;

import java.util.List;

/**
 * Created by yuemelyanov on 04.01.14.
 */
public class SparqlJenaDataSourceProvider implements JRDataSourceProvider {

    private static class SparqlField extends JRBaseField {
        private SparqlField(String name, String description) {
            this.name = name;
            this.description = description;
            this.valueClass = String.class;
            this.valueClassName = String.class.getName();
        }
    }

    private String sparqlEndpoint;

    public SparqlJenaDataSourceProvider(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public SparqlJenaDataSourceProvider() {
        this("http://example.com/sparql");
    }

    @Override
    public boolean supportsGetFieldsOperation() {
        return true;
    }

    @Override
    public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException {
        JRQuery query = report.getQuery();
        JRField[] jrFields = null;
        if (query.getLanguage().equalsIgnoreCase("sparql")) {

            QueryExecution queryExecution = QueryExecutionFactory.create(query.getText());
            List<Var> vars = queryExecution.getQuery().getProjectVars();

            jrFields = new JRField[vars.size()];
            for (int i = 0; i < vars.size(); i++) {
                Var var = vars.get(i);
                jrFields[i] = new SparqlField(var.getVarName(), "Field '" + var.getVarName() + "'");
            }

        }
        return jrFields;
    }

    @Override
    public JRDataSource create(JasperReport report) throws JRException {
        if (report.getQuery().getLanguage().equalsIgnoreCase("sparql")) {
            SparqlJenaDataSource instance = new SparqlJenaDataSource(sparqlEndpoint, report.getQuery().getText());
            return instance;
        } else {
            throw new JRException("Expected sparql language, but got " + report.getQuery().getLanguage());
        }
    }

    @Override
    public void dispose(JRDataSource dataSource) throws JRException {

    }
}
