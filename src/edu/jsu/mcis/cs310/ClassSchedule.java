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
        // Iterate through CSV
        Iterator<String[]> iterator = csv.iterator();
        
        // Declaring main topics in json and intializing hashmaps
        LinkedHashMap<String, String> jsonSchedules = new LinkedHashMap<>();
        LinkedHashMap<String, String> jsonSubjects = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashMap<String, Object>> jsonCourses = new LinkedHashMap<>();
        
        // Arraylist for the Schedules, Subjects, and Courses
        ArrayList<String> scheduleArrayCount = new ArrayList<>();
        ArrayList<String> subjectArrayCount = new ArrayList<>();
        ArrayList<String> courseArrayCount = new ArrayList<>();
        
        ArrayList<String> crnArrayCount = new ArrayList<>();
        JsonArray sectionArray = new JsonArray();
        
        if (iterator.hasNext()){
            String[] headings = iterator.next(); //extract column headers
            while (iterator.hasNext()){
                String[] csvRecord = iterator.next(); //extract from each row
                
                // Extracting needed information from CSV
                String recType = csvRecord[5];
                String recHow = csvRecord[11];
                String recAbbv = csvRecord[2].substring(0,3).replaceAll("\\s", "");
                String recName = csvRecord[1];
                String recNum = csvRecord[2].substring(csvRecord[2].length()- 3);
                String recDesc = csvRecord[3];
                int recCredits = Integer.parseInt(csvRecord[6]);
                int recCrnInt = Integer.parseInt(csvRecord[0]);
                String recCrn = csvRecord[0];
                String recSect = csvRecord[4];
                String recStart = csvRecord[7];
                String recEnd = csvRecord[8];
                String recDays = csvRecord[9];
                String recWhere = csvRecord[10];
                String recNameAndNum = csvRecord[2];
                
                // intrusctors
                String[] recInstructors = csvRecord[12].split(",");
                ArrayList<String> instructors = new ArrayList<>();
                for (String instructor : recInstructors){
                    instructors.add(instructor.trim());
                }
                
                // add schedule type if needed
                if(!scheduleArrayCount.contains(recType)){
                    scheduleArrayCount.add(recType);
                    jsonSchedules.put(recType, recHow);
                }
                
                // add subject if needed
                if(!subjectArrayCount.contains(recDesc)){
                    subjectArrayCount.add(recDesc);
                    jsonSubjects.put(recAbbv, recName);
                }
                
                // add course detail if needed
                if (!courseArrayCount.contains(recNameAndNum)){
                    courseArrayCount.add(recNameAndNum);
                    
                    LinkedHashMap<String, Object> jsonCourseDetails = new LinkedHashMap<>();
                    jsonCourseDetails.put("subjectId", recAbbv);
                    jsonCourseDetails.put("courseNum", recNum);
                    jsonCourseDetails.put("description", recDesc);
                    jsonCourseDetails.put("credits", recCredits);
                    
                    jsonCourses.put(recNameAndNum, jsonCourseDetails);
                }
                
                //Add section details if needed
                if(!crnArrayCount.contains(recCrn)){
                    crnArrayCount.add(recCrn);
                    
                    LinkedHashMap<String, Object> jsonSectionDetails = new LinkedHashMap<>();
                    jsonSectionDetails.put("crn", recCrnInt);
                    jsonSectionDetails.put("subjectID", recAbbv);
                    jsonSectionDetails.put("courseNum", recNum);
                    jsonSectionDetails.put("section", recSect);
                    jsonSectionDetails.put("type", recType);
                    jsonSectionDetails.put("startTime", recStart);
                    jsonSectionDetails.put("endTime", recEnd);
                    jsonSectionDetails.put("days", recDays);
                    jsonSectionDetails.put("location", recWhere);
                    jsonSectionDetails.put("instructors", instructors);
                    
                    sectionArray.add(jsonSectionDetails);
                    
                }
                        
                
            }
        }
        
        // Creating JSON object
        JsonObject mainRecord = new JsonObject();
        mainRecord.put("scheduleType", jsonSchedules);
        mainRecord.put("subjects", jsonSubjects);
        mainRecord.put("courses", jsonCourses);
        mainRecord.put("sections", sectionArray);
        
        // Serialize JSON to a string and print
        String jsonString = Jsoner.serialize(mainRecord);
        System.out.println(jsonString);
        
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