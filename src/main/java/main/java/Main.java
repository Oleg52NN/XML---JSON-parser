package main.java;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        writeString(listToJson(list), "data.json");
        List<Employee> listXML = parseXML("data.xml");
        writeString(listToJson(listXML), "data2.json");
    }

    private static List<Employee> parseXML(String s) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(s));
        Node root = doc.getDocumentElement();
        List<Employee> list = new ArrayList<>();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                Employee employee = new Employee();
                employee.setId(Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()));
                employee.setFirstName(element.getElementsByTagName("firstName").item(0).getTextContent());
                employee.setLastName(element.getElementsByTagName("lastName").item(0).getTextContent());
                employee.setCountry(element.getElementsByTagName("country").item(0).getTextContent());
                employee.setAge(Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()));
                list.add(employee);
            }
        }


        return list;
    }


    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        CsvToBean<Employee> csv = new CsvToBean<Employee>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            List<Employee> list = csv.parse(setColumMapping(columnMapping), csvReader);
            return list;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static String listToJson(List<Employee> employeeList) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(employeeList, listType);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ColumnPositionMappingStrategy<Employee> setColumMapping(String[] columnMapping) {
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);
        return strategy;
    }
}


