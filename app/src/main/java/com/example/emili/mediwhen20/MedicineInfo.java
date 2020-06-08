package com.example.emili.mediwhen20;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MedicineInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
//The following code is retrieved and modified from  https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable
        final Medicine mymed = (Medicine) bundle.getSerializable("value");
//ends
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//The following code was retrieved and modified from https://www.youtube.com/watch?v=3CotB4pclTw
                AlertDialog.Builder question = new AlertDialog.Builder(MedicineInfo.this);
                question.setMessage("Ar tikrai norite ištrinti elementą?").setCancelable(false);
                question.setPositiveButton("Taip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMed(mymed.getId());
                        finish();
                        openMain();
                    }
                });
                question.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                question.create().show();

            }
//ends
        });

        //the following lines set the values of the UI elements according to the information of the clicked item

        TextView nameTopBar = findViewById(R.id.name_of_med_top_bar);
        nameTopBar.setText(mymed.getNameOfMed());

        TextView dateCreateVal = findViewById(R.id.date_create_val);
        dateCreateVal.setText(mymed.getDate());

        TextView courseTypeVal = findViewById(R.id.course_type_val);
        courseTypeVal.setText(mymed.getCourse());

        TextView timesPerDayVal = findViewById(R.id.times_a_day_val);
        String routine = "";
        int dailyIntake = 0;
        if (mymed.getMor() == true){
            routine += "ryte ";
            dailyIntake++;
        }
        if (mymed.getDay() == true){
            routine += "dieną ";
            dailyIntake++;
        }
        if (mymed.getEve() == true){
            routine += "vakare";
            dailyIntake++;
        }
        timesPerDayVal.setText(routine);

        TextView howManyTabVal = findViewById(R.id.how_many_tab_val);
        howManyTabVal.setText(String.valueOf(mymed.getHowMany()));

        TextView endOfCousre = findViewById(R.id.finish_date_val);
        String endDate = getEndOfCourse(mymed.getDate(), dailyIntake, mymed.getHowMany(), mymed.getCourse());
        endOfCousre.setText(endDate);

        TextView remainTab = findViewById(R.id.remain_val);
        remainTab.setText(getRemain(mymed.getDate(), dailyIntake, mymed.getHowMany(), mymed.getCourse()));
    }

    String getEndOfCourse(String startingDate, int intake, int tabs, String course){//retrieves a date, when the treatment will end
        int days = (int) Math.ceil((double)tabs/(double)intake);

//The following code was retrieved and modified from https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android/12995000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(startingDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (course.equals("Standartinis")) {//checks the course type
            c.add(Calendar.DATE, days);
            return sdf.format(c.getTime());
        }
//ends
        if (days<=21){//if the course type ir Special and the days are less or equal to 21, then no pause of 7 days needs to occur
            c.add(Calendar.DATE, days);
            return sdf.format(c.getTime());
        }

        while (days>-1){//sets the date of the end of the Special course to an additional 7 days
            int take = days-21;
            c.add(Calendar.DATE, 28);
            if (take>21){
                continue;
            }
            else{
                c.add(Calendar.DATE, take);
                break;
            }
        }
        return sdf.format(c.getTime());
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
        int daysPassed = (int) days;
//ends
        if (course.equals("Specialus")){//checks if the course type is Special
//The following code was retrieved and modified from https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android/12995000
            Calendar a21 = Calendar.getInstance();
            Calendar a28 = Calendar.getInstance();
            Calendar aC  = Calendar.getInstance();
            try {
                a21.setTime(sdf.parse(startingDate));
                a28.setTime(sdf.parse(startingDate));
                aC.setTime(sdf.parse(startingDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//ends
            a21.add(Calendar.DATE, 21);
            a28.add(Calendar.DATE, 28);
            aC.add(Calendar.DATE, 49);
            Date after21 = a21.getTime();
            Date after28 = a28.getTime();
            Date afterC = aC.getTime();
            if (current.after(after21)&&current.before(after28)){//checks if the current date is in the period of the course stop
                int res = tabs-21;
                return String.valueOf(res);
            }
            if (current.after(after28)){//checks if the current date is after the course stop
//The following code was retrieved and modified from https://stackoverflow.com/questions/10690370/how-do-i-get-difference-between-two-dates-in-android-tried-every-thing-and-pos/24279153
                if (current.after(afterC)){//checks if the course has ended or not
                    return String.valueOf(0);
                }
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

    public void deleteMed(int id){//deletes medicine from the file "memory"
        ArrayList <String> infoMeds = new ArrayList<String>();
        try {
//The following code is retrieved and modified from https://www.journaldev.com/9383/android-internal-storage-example-tutorial
            FileInputStream fin = openFileInput("memory");

            int c;
            String temp="";
            while( (c = fin.read()) != -1){//reads the file contents
                if (c == '\n'){
                    if (temp.length() > 0){
                        infoMeds.add(temp);//puts each line of strings into ArrayList
                    }
                    temp = "";
                }else {
                    temp = temp + Character.toString((char) c);
                }
            }
//ends

            for (int i = 0; infoMeds.size()>i; i++){
                String line = infoMeds.get(i);
                int howManyComas = 0;
                int startOfSub = 0;
                for (int k = 0; line.length()>k; k++){
                    if (line.charAt(k) == ',' && howManyComas == 6){//checks if the 6th coma is reached, where the id of the object is
                        String idFromArray = line.substring(startOfSub, k);
                        int idArray = Integer.parseInt(idFromArray);
                        if (idArray==id){//according to the id number, that value will be removed from the ArrayList
                            infoMeds.remove(i);
                            break;
                        }
                    }
                    if (line.charAt(k) == ','){//checks if the read character is a coma
                        howManyComas++;
                        startOfSub = k+1;
                    }

                }
            }

            rewriteMeds(infoMeds);
            fin.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rewriteMeds (ArrayList<String> infoMeds){//writes the modified information without the deleted item
            String allElems = "";
            for (int i = 0; i<infoMeds.size(); i++){
                allElems += infoMeds.get(i) + '\n';
            }

        try {
//This code was retrieved and modified from  https://www.journaldev.com/9383/android-internal-storage-example-tutorial
            //writes into the file, overwriting the past information
            FileOutputStream outputStream = openFileOutput("memory", Context.MODE_PRIVATE);
            outputStream.write(allElems.getBytes());

            outputStream.close();
//ends
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void openMain(){//opens the MainList activity
        setResult(Activity.RESULT_OK);
        finish();
    }

}
