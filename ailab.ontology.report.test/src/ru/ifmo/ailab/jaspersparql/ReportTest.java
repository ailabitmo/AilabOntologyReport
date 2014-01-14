package ru.ifmo.ailab.jaspersparql;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuemelyanov on 04.01.14.
 */
public class ReportTest {
    public static void main(String[] args)  {
        try {
            LinkedLearningCourseReport();
//            testDatasourceProvider();
            //testReportGeneration();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }


    private static void testDatasourceProvider() throws JRException {

        SparqlJenaDataSourceProvider provider = new SparqlJenaDataSourceProvider("");
        JasperReport report = (JasperReport) JRLoader.loadObjectFromFile("testReport.jasper");
        provider.getFields(report);

    }

    private static void testReportGeneration() {
        String sparqlEndpoint = "http://linkedlearning.tk/sparql";
        String query = "PREFIX learningRu: <http://www.semanticweb.org/k0shk/ontologies/2013/5/learning#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT ?courseName ?moduleName ?number WHERE {\n" +
                "   ?course learningRu:hasModule ?iri;\n" +
                "           rdfs:label ?courseName.\n" +
                "    ?iri learningRu:numberOfModule  ?number .\n" +
                " OPTIONAL {?iri rdfs:label ?moduleName . \n" +
                "  FILTER (langMatches(lang(?moduleName), \"ru\")) } \n" +
                "  OPTIONAL {?iri rdfs:label ?moduleName } }";
        String reportSource = "testReport.jasper";

        SparqlJenaDataSource ds = new SparqlJenaDataSource(sparqlEndpoint, query);

        Map parameters = new HashMap();
        try {
            JasperFillManager.fillReportToFile(
                    reportSource,
                    parameters,
                    ds);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private static void LinkedLearningCourseReport() throws JRException {
        String sparqlEndpoint = "http://77.234.221.102:8888/sparql";
        String commonPrefix = "PREFIX learningRu: <http://www.semanticweb.org/k0shk/ontologies/2013/5/learning#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX aiiso: <http://purl.org/vocab/aiiso/schema#>\n";
        String courseIRI = "learningRu:AnalyticGeometryAndLinearAlgebra";
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

        String literatureQuery = commonPrefix +
                "SELECT DISTINCT ?Произведение\n" +
                " WHERE { " + courseIRI + " learningRu:hasResource ?Произведение .}\n" +
                " ORDER BY ASC(?Произведение)";

        String moduleQuery = commonPrefix +
                "SELECT DISTINCT ?module ?moduleName ?moduleOrder\n" +
                " WHERE { \n" +
                "   {  " + courseIRI + " learningRu:hasModule ?module } \n" +
                "   UNION { ?module learningRu:isModuleOf  " + courseIRI + " } . \n" +
                "   ?module rdfs:label ?moduleName .   \n" +
                "   ?module learningRu:numberOfModule ?moduleOrder \n" +
                "   FILTER (langMatches(lang(?moduleName), \"ru\") || LANG(?moduleName) = \"\") }\n" +
                " ORDER BY ASC(?moduleOrder)";
        String moduleIRI = "learningRu:VectorAlgebra";
        String lectureQuery = commonPrefix + "SELECT ?lection ?lectionName (GROUP_CONCAT(?termLabel; separator=\", \") as ?terms) \n" +
                " WHERE { \n" +
                "?module a aiiso:Module;\n" +
                "{ ?module learningRu:hasLecture ?lection } UNION { ?lection learningRu:isLectureOf ?module } . \n" +
                "?lection learningRu:numberOfLecture ?number; \n" +
                "# learningRu:previousLecture ?prevLection ;\n" +
                " rdfs:label ?lectionName.\n" +
                "#?prevLection rdfs:label ?prevLectionName.\n" +
                "?term learningRu:isTermOf ?lection;\n" +
                "      rdfs:label ?termLabel.\n" +
                "FILTER(?module = " + moduleIRI + ")\n" +
                "}\n" +
                "group by ?lection ?lectionName\n" +
                " ORDER BY ASC(?number)";
        String termsQuery = commonPrefix +"SELECT ?term ?termName (GROUP_CONCAT(?lectureName; separator=\", \") as ?lectures) \n" +
                " WHERE { \n" +
                "{?course learningRu:hasModule ?module} UNION {?module learningRu:isModuleOf ?course}\n" +
                "{?module learningRu:hasLecture ?lecture } UNION { ?lecture learningRu:isLectureOf ?module } . \n" +
                "{?term learningRu:isTermOf ?lecture } UNION  { ?lecture learningRu:hasTerm ?term}\n" +
                "?lecture rdfs:label ?lectureName.\n" +
                "?term rdfs:label ?termName.\n" +
                "FILTER(?course = learningRu:AnalyticGeometryAndLinearAlgebra &&\n" +
                "(langMatches(lang(?lectureName), \"ru\") || LANG(?lectureName) = \"\") &&\n" +
                "      (langMatches(lang(?termLabel), \"ru\") || LANG(?termLabel) = \"\")" +
                ")}\n" +
                "group by ?term ?termName\n"+
                "having bound(?term)";

        SparqlJenaDataSource ds = new SparqlJenaDataSource(sparqlEndpoint, courseQuery);

        JasperCompileManager.compileReportToFile("CourseReport.jrxml", "CourseReport.jasper");
        JasperCompileManager.compileReportToFile("LiteratureSubreport.jrxml", "LiteratureSubreport.jasper");
        JasperCompileManager.compileReportToFile("ModuleSubreport.jrxml", "ModuleSubreport.jasper");
        JasperCompileManager.compileReportToFile("LectureSubreport.jrxml", "LectureSubreport.jasper");
        JasperCompileManager.compileReportToFile("TermSubreport.jrxml", "TermSubreport.jasper");

        Map parameters = new HashMap();

        JasperFillManager.fillReportToFile(
                "CourseReport.jasper",
                parameters,
                ds);

    }
}
