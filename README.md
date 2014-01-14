Ailab Ontology Report
=====================

Goal
____

Our goal is to provide simple and powerful reporting for semantic data sources using proven and stable technologies.

Problem
____

There are very few decent solutions that bring semantic web technologies to end-users and at the same time capable of solving everyday tasks. It prevents the widespread use of semantic web technologies. Like many other strongly awaited and yet practically absent products in semantic web world is a reporting tool, which is widely accepted in RDMS industry. Reports are still playing a significant role of presenting and visualizing data to user. The lack of powerful and simple reporting software solution limits the speed of semantic web adoption in non-scientific world.

Solution
____
We’ll use the proven solutions in semantic web and reporting technologies to create the stable and easy-to-use reporting tool, providing smooth user experience. Currently we’ve chosen Jena for sparql handling and Jasper reports for report generation.

Usage, first stage
____
1. Create report in JasperReport Studio or iReport, create corresponding fields for projection variables in sparql query
2. Compile report and put it into your application
3. Create SparqlJenaDataSource instance with sparql endpoint and query
4. Use Jasper helpers to fill the report with created SparqlJenaDataSource instance with data

Usage, second stage
____
1. Create DataAdapter in JasperReport Studio with sparql endpoint and query
2. Create report referencing DataAdapter, it will use fields from sparql query automatically
3. Sparql query will be stored within the report file
3. Execute the report with data in JasperReport Studio
4. Use compiled report in your application, passing only sparql endpoint and user credentials as parameters to report generation

Tasks
____
Stage 1 is complete, tutorial and examples are coming.
Stage 2 requires creation of JasperReport Studio Eclipse plug-ins to create new DataAdapter type, editing controls, etc.
