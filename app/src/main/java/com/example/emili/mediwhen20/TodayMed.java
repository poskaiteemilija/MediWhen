package com.example.emili.mediwhen20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodayMed extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        ImageButton info = findViewById(R.id.info_button);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Šiame lange galite pamatyti, kokius vaistus reikės šiandien vartoti", Toast.LENGTH_LONG).show();//writes the information about the content of this window
            }
        });
        listView = findViewById(R.id.today_med_list);

        updateList();
    }

    public void updateList(){//instantiates the CustomArrayAdapter class
        final ArrayList <Medicine> finalList = readFromFile();
        ArrayList <String> namesList = namesOfDrugs(finalList);
        ArrayList<String> remainingDays = listOfTabs(finalList);
        ArrayList<String> routines = listOfRout(finalList);
        ArrayList<String> images = listOfImages(finalList);

        CustomArrayAdapter adapter = new CustomArrayAdapter(this, namesList, remainingDays, routines, images);
        listView.setAdapter(adapter);
    }

    public ArrayList<Medicine> readFromFile () {//reads form file "memory"
        ArrayList<Medicine> medicines = new ArrayList<>();
//The following code is retrieved and modified from https://www.journaldev.com/9383/android-internal-storage-example-tutorial
        try {
            FileInputStream fin = openFileInput("memory");

            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                if (c == '\n'){//checks if there is a new line, which will indicate about a new object in the list
                    if (temp.length() > 0){//checks if temp is not empty
                        Medicine newmed = parseLine(temp);//uses method parseLine to convert String values into object type Medicine
                        medicines.add(newmed);
                    }
                    temp = "";
                }else {
                    temp = temp + Character.toString((char) c);
                }
            }
//ends
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return medicines;
    }

    private Medicine parseLine(String line){//converts a read-from-file line into an object type Medicine
        int first = 0;
        int index = 0;
        String[] params = new String[8];
        for (int i = 0; line.length() > i; i++){
            if (line.charAt(i) == ','){//checks if the separator symbol is met, which is ','
                String variable = line.substring(first, i);
                first = i+1;
                params[index] = variable;
                index++;
            }
        }
        boolean mor = false,
                day = false,
                eve = false;
        //three if statements, which check whether the check boxes are checked
        if (params[2].equals("true")){ mor = true; }
        if (params[3].equals("true")){ day = true; }
        if (params[4].equals("true")){ eve = true; }
        return new Medicine(params[0], params[1], mor, day, eve, Integer.parseInt(params[5]), Integer.parseInt(params[6]), params[7]);
    }

    public  ArrayList <String> namesOfDrugs (ArrayList <Medicine> meds){//creates an ArrayList of drug names, which is used by the CustomArrayAdapter
        ArrayList <String> names = new ArrayList<>();
        for (int i = 0; meds.size()>i; i++){
            names.add(meds.get(i).getNameOfMed());
        }
        return names;
    }


    String getRemain (String startingDate, int intake, int tabs, String course){//gets remaining number of tablets
//The following code was retrieved and modified from https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android/12995000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date current = Calendar.getInstance().getTime();
        Date start = null;
        try {
            start = sdf.parse(startingDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//ends
        if (start == null || current.before(start)){//checks if the starting date is later than the current date
            return String.valueOf(tabs);
        }

//The following code was retrieved and modified from https://stackoverflow.com/questions/10690370/how-do-i-get-difference-between-two-dates-in-android-tried-every-thing-and-pos/24279153
        long difference = start.getTime()-current.getTime();
        long seconds = difference / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
//ends
        int daysPassed = (int) days;

        if (course.equals("Specialus")){//checks if the course type is Special
//The following code was retrieved and modified from https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android/12995000
            Calendar a21 = Calendar.getInstance();
            Calendar a28 = Calendar.getInstance();
            try {
                a21.setTime(sdf.parse(startingDate));
                a28.setTime(sdf.parse(startingDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//ends
            a21.add(Calendar.DATE, 21);
            a28.add(Calendar.DATE, 28);
            Date after21 = a21.getTime();
            Date after28 = a28.getTime();
            if (current.after(after21)&&current.before(after28)){//checks if the current date is in the period of the course stop
                int res = tabs-21;
                return String.valueOf(res);
            }
            if (current.after(after28)){//checks if the current date is after the period of the course stop
//The following code was retrieved and modified from https://stackoverflow.com/questions/10690370/how-do-i-get-difference-between-two-dates-in-android-tried-every-thing-and-pos/24279153
                long ndDiff = current.getTime()-after28.getTime();
                long seconds2 = ndDiff / 1000;
                long minutes2 = seconds2 / 60;
                long hours2 = minutes2 / 60;
                long days2 = hours2 / 24;
//ends
                int daysA28 = (int) days2;
                int res = (tabs - 21) - intake*Math.abs(daysA28);
                return String.valueOf(res);
            }
        }

        int res = tabs - intake*Math.abs(daysPassed);
        if (res<=0){//checks if the value of tablets is 0 or less
            return String.valueOf(0);
        }
        return String.valueOf(res);
    }

    String getValueForImage (String startingDate, int intake, int tabs, String course){//gets values indicating about the course status icon
        int days = (int) Math.ceil((double)tabs/(double)intake);

//The following code was retrieved and modified from https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android/12995000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(startingDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (course.equals("Standartinis")) {//checks if the course is standard
            c.add(Calendar.DATE, days);
            Calendar cur = Calendar.getInstance();
            if (c.after(cur)) {//checks if the end date of the course is after the current date
                return "green";
            }
            else{
                return "red";
            }
        }
//ends
        if (days<=21){//checks if the special course type has 21 or less days
            c.add(Calendar.DATE, days);
            return "green";
        }

        if (course.equals("Specialus")){//checks if the course type is special
//The following code was retrieved and modified from https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android/12995000
            Calendar a21 = Calendar.getInstance();
            Calendar a28 = Calendar.getInstance();
            try {
                a21.setTime(sdf.parse(startingDate));
                a28.setTime(sdf.parse(startingDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//ends
            a21.add(Calendar.DATE, 21);
            a28.add(Calendar.DATE, 28);
            Date after21 = a21.getTime();
            Date after28 = a28.getTime();
            Date current = Calendar.getInstance().getTime();
            if (current.after(after21)&&current.before(after28)){//checks if the current date is in the period of the course stop
                return "yellow";
            }
            if (c.after(after28)){//checks if the current date is after the course stop
                return "green";
            }
        }

        return "green";
    }

    ArrayList <String> listOfTabs (ArrayList<Medicine> meds){//creates an ArrayList of how many tablets are still left
        ArrayList<String> wholeTabs = new ArrayList<>();
        for (int i = 0; i<meds.size(); i++){
            int dailyIntake = 0;
            //three if statements which determine how many times a day a person needs to take the medicine
            if (meds.get(i).getMor() == true){
                dailyIntake++;
            }
            if (meds.get(i).getDay() == true){
                dailyIntake++;
            }
            if (meds.get(i).getEve() == true){
                dailyIntake++;
            }
            String tabs = getRemain(meds.get(i).getDate(), dailyIntake, meds.get(i).getHowMany(), meds.get(i).getCourse());
            wholeTabs.add(tabs);
        }
        return wholeTabs;
    }

    ArrayList <String> listOfRout (ArrayList<Medicine> meds){//creates an ArrayList of when to take the medicine
        ArrayList<String> routine = new ArrayList<>();
        for (int i = 0; i<meds.size(); i++){
            String rout = "";
            //three if statements which put out a string of when to take the medicine
            if (meds.get(i).getMor() == true){
                rout += "ryte ";
            }
            if (meds.get(i).getDay() == true){
                rout += "dieną ";
            }
            if (meds.get(i).getEve() == true){
                rout += "vakare";
            }
            routine.add(rout);
        }
        return routine;
    }

    ArrayList <String> listOfImages (ArrayList<Medicine> meds){//creates an ArrayList of status icon values
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i<meds.size(); i++){
            int dailyIntake = 0;
            //three if statements which determine how many times a day a person needs to take the medicine
            if (meds.get(i).getMor() == true){
                dailyIntake++;
            }
            if (meds.get(i).getDay() == true){
                dailyIntake++;
            }
            if (meds.get(i).getEve() == true){
                dailyIntake++;
            }
            String tabs = getValueForImage(meds.get(i).getDate(), dailyIntake, meds.get(i).getHowMany(), meds.get(i).getCourse());
            images.add(tabs);
        }
        return images;
    }
}
