package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        
        Iterator<String[]> csvIterator = csv.iterator(); // Iterating through CSV

    LinkedHashMap<String, String> scheduleTypes = new LinkedHashMap<>(); // HashMap of acronym (type) to full name (where)
    LinkedHashMap<String, String> subjectNames = new LinkedHashMap<>();
    LinkedHashMap<String, HashMap> courseDetails = new LinkedHashMap<>();

    ArrayList<String> scheduleTypeList = new ArrayList<>(); // Creating an ArrayList to store types of schedule types
    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<String> courseList = new ArrayList<>();
    ArrayList<String> crnList = new ArrayList<>();
    ArrayList<HashMap> sectionDetailsList = new ArrayList<>();

    if (csvIterator.hasNext()) {
        String[] headings = csvIterator.next();
        while (csvIterator.hasNext()) {
            String[] csvRecord = csvIterator.next();

            String classType = csvRecord[5]; // CSV record for the type of class abbreviated (online = ONL etc)
            String deliveryMethod = csvRecord[11]; // CSV record for how the class is run (online vs in-person etc)

            String subjectAbbreviation = csvRecord[2].substring(0, 3).replaceAll("\\s", ""); // CSV record for the Subject Description
            String subjectName = csvRecord[1]; // CSV record for the SubjectName

            String courseNumber = csvRecord[2].substring(csvRecord[2].length() - 3);
            String courseDescription = csvRecord[3];
            String fullSubjectName = csvRecord[2];
            int creditHours = Integer.parseInt(csvRecord[6]);

            int crnInt = Integer.parseInt(csvRecord[0]);
            String crnString = csvRecord[0];                
            String section = csvRecord[4];
            String startTime = csvRecord[7];
            String endTime = csvRecord[8];
            String classDays = csvRecord[9];
            String location = csvRecord[10];
            String[] instructorArray = csvRecord[12].split(",");
            ArrayList<String> instructorList = new ArrayList<>();

            for (String instructor : instructorArray) {
                instructorList.add(instructor.replaceAll("^\\s+", "").replaceAll("\\s+$", ""));
            }

            for (int i = 0; i < headings.length; ++i) {
                if (scheduleTypeList.contains(classType)) { // Checking to see if it's already in our list
                    assert true; // Skipping if already in our array
                } else {
                    scheduleTypeList.add(csvRecord[5]); // Adding to ArrayList to be skipped in future
                    scheduleTypes.put(classType, deliveryMethod); // Adding TYPE and WHERE to HashMap
                }

                if (subjectList.contains(courseDescription)) { // Checking to see if it's already in our list
                    assert true; // Skipping if already in our array
                } else {
                    subjectList.add(courseDescription); // Adding to ArrayList to be skipped in future
                    subjectNames.put(subjectAbbreviation, subjectName); // Adding TYPE and WHERE to HashMap
                }

                if (courseList.contains(fullSubjectName)) { // Checking to see if it's already in our list
                    assert true; // Skipping if already in our array
                } else {
                    courseList.add(csvRecord[2]); // Adding to ArrayList to be skipped in future
                    LinkedHashMap<String, Object> courseInfo = new LinkedHashMap<>();
                    courseInfo.put(SUBJECTID_COL_HEADER, subjectAbbreviation);
                    courseInfo.put(NUM_COL_HEADER, courseNumber);
                    courseInfo.put(DESCRIPTION_COL_HEADER, courseDescription);
                    courseInfo.put(CREDITS_COL_HEADER, creditHours);
                    courseDetails.put(fullSubjectName, courseInfo);
                }

                if (crnList.contains(crnString)) { // Checking to see if it's already in our list
                    assert true; // Skipping if already in our array
                } else {
                    crnList.add(crnString); // Adding to ArrayList to be skipped in future
                    LinkedHashMap<String, Object> sectionInfo = new LinkedHashMap<>();
                    sectionInfo.put(CRN_COL_HEADER, crnInt);
                    sectionInfo.put(SUBJECTID_COL_HEADER, subjectAbbreviation);
                    sectionInfo.put(NUM_COL_HEADER, courseNumber);
                    sectionInfo.put(SECTION_COL_HEADER, section);
                    sectionInfo.put(TYPE_COL_HEADER, classType);
                    sectionInfo.put(START_COL_HEADER, startTime);
                    sectionInfo.put(END_COL_HEADER, endTime);
                    sectionInfo.put(DAYS_COL_HEADER, classDays);
                    sectionInfo.put(WHERE_COL_HEADER, location);
                    sectionInfo.put(INSTRUCTOR_COL_HEADER, instructorList);

                    sectionDetailsList.add(sectionInfo);
                }
            }
        }
    }

    JsonObject mainRecord = new JsonObject();
    mainRecord.put("scheduletype", scheduleTypes);
    mainRecord.put(SUBJECT_COL_HEADER, subjectNames);
    mainRecord.put("course", courseDetails);
    mainRecord.put("section", sectionDetailsList);

    String jsonString = Jsoner.serialize(mainRecord);

    // Print final JSON string after all data is added
    // System.out.println(jsonString);

    return jsonString;

}

    
    public String convertJsonToCsvString(JsonObject json) {
        
        
        
    }
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}