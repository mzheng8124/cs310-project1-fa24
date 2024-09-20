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
        
        Iterator<String[]> csvIterator = csv.iterator(); // Iterating through the CSV data
        
        LinkedHashMap<String, String> scheduleTypeMapping = new LinkedHashMap<>(); 
        LinkedHashMap<String, String> subjectNameMapping = new LinkedHashMap<>();
        LinkedHashMap<String, HashMap> courseInformation = new LinkedHashMap<>();

        // ArrayLists to store various data types
        ArrayList<String> uniqueScheduleTypes = new ArrayList<>();  
        ArrayList<String> uniqueSubjects = new ArrayList<>();
        ArrayList<String> uniqueCourses = new ArrayList<>();
        ArrayList<String> uniqueCrns = new ArrayList<>();
        ArrayList<HashMap> sectionDetailsCollection = new ArrayList<>();

        if (csvIterator.hasNext()) {
            String[] headers = csvIterator.next();
                while (csvIterator.hasNext()) {
                    String[] csvRecord = csvIterator.next();

                    String courseType = csvRecord[5]; 
                    String courseDeliveryMethod = csvRecord[11]; 

                    String subjectAbbreviation = csvRecord[2].substring(0, 3).replaceAll("\\s", ""); 
                    String subjectFullName = csvRecord[1];

                    String courseNum = csvRecord[2].substring(csvRecord[2].length() - 3);
                    String courseDescription = csvRecord[3];
                    String completeSubjectName = csvRecord[2];
                    int creditHours = Integer.parseInt(csvRecord[6]);

                    int crnNumeric = Integer.parseInt(csvRecord[0]);
                    String crnString = csvRecord[0];                
                    String sectionNumber = csvRecord[4];
                    String classStartTime = csvRecord[7];
                    String classEndTime = csvRecord[8];
                    String classDays = csvRecord[9];
                    String classLocation = csvRecord[10];
                    String[] instructorArray = csvRecord[12].split(",");
                    ArrayList<String> instructorList = new ArrayList<>();

        for (String instructor : instructorArray) {
            instructorList.add(instructor.replaceAll("^\\s+", "").replaceAll("\\s+$", ""));
        }

        for (int i = 0; i < headers.length; ++i) {
            if (uniqueScheduleTypes.contains(courseType)) { // Checking if course type is already recorded
                assert true; // Skipping if already in the list
            } else {
                uniqueScheduleTypes.add(courseType); // Adding course type to the list
                scheduleTypeMapping.put(courseType, courseDeliveryMethod); // Mapping course type to delivery method
            }

            if (uniqueSubjects.contains(courseDescription)) { // Checking if course description is already recorded
                assert true; // Skipping if already in the list
            } else {
                uniqueSubjects.add(courseDescription); // Adding course description to the list
                subjectNameMapping.put(subjectAbbreviation, subjectFullName); // Mapping abbreviation to full name
            }

            if (uniqueCourses.contains(completeSubjectName)) { // Checking if full subject name is already recorded
                assert true; // Skipping if already in the list
            } else {
                uniqueCourses.add(csvRecord[2]); // Adding complete subject name to the list
                LinkedHashMap<String, Object> courseDetails = new LinkedHashMap<>();
                courseDetails.put(SUBJECTID_COL_HEADER, subjectAbbreviation);
                courseDetails.put(NUM_COL_HEADER, courseNum);
                courseDetails.put(DESCRIPTION_COL_HEADER, courseDescription);
                courseDetails.put(CREDITS_COL_HEADER, creditHours);
                courseInformation.put(completeSubjectName, courseDetails);
            }

            if (uniqueCrns.contains(crnString)) { // Checking if CRN is already recorded
                assert true; // Skipping if already in the list
            } else {
                uniqueCrns.add(crnString); // Adding CRN to the list
                LinkedHashMap<String, Object> sectionDetails = new LinkedHashMap<>();
                sectionDetails.put(CRN_COL_HEADER, crnNumeric);
                sectionDetails.put(SUBJECTID_COL_HEADER, subjectAbbreviation);
                sectionDetails.put(NUM_COL_HEADER, courseNum);
                sectionDetails.put(SECTION_COL_HEADER, sectionNumber);
                sectionDetails.put(TYPE_COL_HEADER, courseType);
                sectionDetails.put(START_COL_HEADER, classStartTime);
                sectionDetails.put(END_COL_HEADER, classEndTime);
                sectionDetails.put(DAYS_COL_HEADER, classDays);
                sectionDetails.put(WHERE_COL_HEADER, classLocation);
                sectionDetails.put(INSTRUCTOR_COL_HEADER, instructorList);

                sectionDetailsCollection.add(sectionDetails);
            }
        }
    }
}

    JsonObject mainRecord = new JsonObject();
    mainRecord.put("scheduletype", scheduleTypeMapping);
    mainRecord.put(SUBJECT_COL_HEADER, subjectNameMapping);
    mainRecord.put("course", courseInformation);
    mainRecord.put("section", sectionDetailsCollection);

    String jsonString = Jsoner.serialize(mainRecord);

// Print final JSON string after all data is added
// System.out.println(jsonString);

    return jsonString;


}

    
    public String convertJsonToCsvString(JsonObject json) {
       // note to self: use string writer and cvs writer
        List<String[]> courseSchedule = new ArrayList();  // List to hold all rows for the result

    // Base headers for the CSV output
    String[] csvHeader = {
    CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, 
    DESCRIPTION_COL_HEADER, SECTION_COL_HEADER, TYPE_COL_HEADER, 
    CREDITS_COL_HEADER, START_COL_HEADER, END_COL_HEADER, DAYS_COL_HEADER,
    WHERE_COL_HEADER, SCHEDULE_COL_HEADER, INSTRUCTOR_COL_HEADER
};

// Add header row to the result list
courseSchedule.add(csvHeader);

// Print header (could be for debugging purposes)
System.out.println(csvHeader);

// Retrieve base objects from the input JSON
JsonObject scheduleTypesMap = (JsonObject) json.get("scheduletype");  // Schedule types map from JSON
JsonObject subjectMap = (JsonObject) json.get("subject");             // Subjects map from JSON
JsonObject coursesMap = (JsonObject) json.get("course");              // Courses map from JSON
JsonArray sectionsArray = (JsonArray) json.get("section");            // Array of sections

// Loop through each section in the JSON array
for (int i = 0; i < sectionsArray.size(); i++) {

    // Array to hold individual row data (one line of CSV output)
    String[] csvRow = new String[13];

    // Current section object
    JsonObject currentSection = (JsonObject) sectionsArray.get(i);

    // Retrieve the associated course using subject id and course number
    JsonObject associatedCourse = (JsonObject) coursesMap.get(
        currentSection.get("subjectid").toString() + " " + currentSection.get("num").toString()
    );

    // Get the instructors for the current section
    JsonArray instructorsArray = (JsonArray) currentSection.get("instructor");

    // Populate row data with details
    csvRow[0] = currentSection.get("crn").toString();                            // CRN
    csvRow[1] = subjectMap.get(currentSection.get("subjectid").toString()).toString();  // Subject
    csvRow[2] = currentSection.get("subjectid").toString() + " " + currentSection.get("num").toString(); // Course Number
    csvRow[3] = associatedCourse.get("description").toString();                  // Course Description
    csvRow[4] = currentSection.get("section").toString();                        // Section
    csvRow[5] = currentSection.get("type").toString();                           // Type of Class
    csvRow[6] = associatedCourse.get("credits").toString();                      // Credits
    csvRow[7] = currentSection.get("start").toString();                          // Start Time
    csvRow[8] = currentSection.get("end").toString();                            // End Time
    csvRow[9] = currentSection.get("days").toString();                           // Days of Week
    csvRow[10] = currentSection.get("where").toString();                         // Location
    csvRow[11] = scheduleTypesMap.get(csvRow[5]).toString();                     // Schedule Type

    // Process instructors into a comma-separated string
    StringBuilder instructors = new StringBuilder();
    for (int j = 0; j < instructorsArray.size(); j++) {
        if (j == 0) {
            instructors.append(instructorsArray.get(j).toString());
        } else {
            instructors.append(", ").append(instructorsArray.get(j).toString());
        }
    }
    csvRow[12] = instructors.toString();                                         // Instructors

    // Add the completed row to the result list
    courseSchedule.add(csvRow);
}

// Convert the result list into a CSV string and return it
return getCsvString(courseSchedule);

        
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