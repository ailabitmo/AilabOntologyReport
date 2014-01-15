package ru.ifmo.ailab.jaspersparql.web;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import ru.ifmo.ailab.jaspersparql.SparqlJenaDataSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuemelyanov on 15.01.14.
 */
public class ReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String sparqlEndpoint = "http://77.234.221.102:8888/sparql";
        String commonPrefix = "PREFIX learningRu: <http://www.semanticweb.org/k0shk/ontologies/2013/5/learning#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX aiiso: <http://purl.org/vocab/aiiso/schema#>\n";

        String courseQuery = commonPrefix +
                "SELECT ?course ?courseName (count(?module) as ?moduleCount)  WHERE {                         \n" +
                " ?course a aiiso:Course;\n" +
                "      rdfs:label ?courseName .         \n" +
                "  FILTER (\n" +
//                "      ?course = " + courseIRI + " &&\n" +
                "      (langMatches(lang(?courseName), \"ru\") || LANG(?courseName) = \"\")) \n" +
                "  OPTIONAL {\n" +
                "      { ?course learningRu:hasModule ?module } UNION { ?module learningRu:isModuleOf  ?course } \n" +
                "   } \n" +
                "} \n" +
                "group by ?course ?courseName";

        SparqlJenaDataSource ds = new SparqlJenaDataSource(sparqlEndpoint, courseQuery);

        try {

            Map parameters = new HashMap();

            resp.setContentType("application/octet-stream");
            resp.addHeader("Content-Disposition", "attachment; filename=CourseReport.docx;");

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    req.getServletContext().getResourceAsStream("/WEB-INF/classes/CourseReport.jasper"),
                    parameters,
                    ds);

            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, resp.getOutputStream());
            exporter.exportReport();

        } catch (JRException e) {
            e.printStackTrace();
        }

    }
}
