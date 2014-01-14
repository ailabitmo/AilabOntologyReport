package ru.ifmo.ailab.jaspersparql;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * Created by yuemelyanov on 04.01.14.
 */
public class SparqlJenaDataSource implements JRDataSource, JRRewindableDataSource {

    private ResultSet resultSet;
    private QuerySolution querySolution;
    private QueryExecution queryExecution;
    private String endpoint;

    public SparqlJenaDataSource(String endpoint, String query) {
        this.endpoint = endpoint;
        initQueryExecution(endpoint, query);
    }

    private void initQueryExecution(String endpoint, String query) {
        try {
            this.queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
            doQuery();
        } catch (QueryParseException qpe) {
            System.out.println("Error parsing query\n" + query);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            qpe.printStackTrace();
            throw qpe;
        }
    }

    private void doQuery() {
        resultSet = queryExecution.execSelect();
    }

    public SparqlJenaDataSource(QueryExecution queryExecution) {
        this.queryExecution = queryExecution;
        doQuery();
    }

    private volatile int nextLaunches = 0;
    @Override
    public boolean next() throws JRException {
        nextLaunches++;
        if (resultSet.hasNext()) {
            querySolution = resultSet.nextSolution();
            return true;
        } else return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        try {
            RDFNode node = querySolution.get(jrField.getName());
            if (node == null) return null;
            if (node.isLiteral()) {
                Literal l = node.asLiteral();
                return l.getValue();
            } else {
                Resource r = node.asResource();
                return r.getURI();
            }
        } catch (Exception e) {
            throw new JRException("Error getting field " + jrField.getName() + " value", e);
        }
    }

    @Override
    public void moveFirst() throws JRException {
        try {
            resultSet = queryExecution.execSelect();
        } catch (Exception e) {
            throw new JRException("Error rewinding dataset", e);
        }
    }

    public JRDataSource SubreportDataSource(String query) {
        return new SparqlJenaDataSource(endpoint, query);
    }
}
