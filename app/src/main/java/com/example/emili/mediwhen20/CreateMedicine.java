package com.example.emili.mediwhen20;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CreateMedicine extends AppCompatActivity {

    TextView showMeDialog;
    DatePickerDialog.OnDateSetListener pickADate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_medicine_layout);

//The following code is retrieved and modified from https://www.youtube.com/watch?v=hwe1abDO2Ag
        showMeDialog = findViewById(R.id.starting_day);
        showMeDialog.setPaintFlags(showMeDialog.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final TextView date = findViewById(R.id.date);

        showMeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentDate = Calendar.getInstance();
                int y = currentDate.get(Calendar.YEAR);
                int m = currentDate.get(Calendar.MONTH);
                int d = currentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog pickDate = new DatePickerDialog(CreateMedicine.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, pickADate, y, m, d);
                pickDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pickDate.show();
            }
        });


        pickADate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String pickeddate = year+"/"+(month < 10 ? "0" : "")+month+"/"+(day < 10 ? "0" : "")+day;
                date.setText(pickeddate);
            }
        };
//ends

        Button save = findViewById(R.id.save_button);

        save.setOnClickListener(new View.OnClickListener() {//click gets data from each widget by calling method getInfoFromWidgets
            @Override
            public void onClick(View view) {
                int e = chekForPossibleErrors();
                if (e==0) {
                    getInfoFromWidgets();
                    openMain();
                }
                else{
                    int whole = errors.length();
                    String finalE = errors.substring(0, whole-1);
                    Toast.makeText(CreateMedicine.this, finalE, Toast.LENGTH_LONG).show();//displays all the errors that the user has made
                    openMain();
                }
            }
        });
    }

    public void openMain(){//opens MainList activity
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void getInfoFromWidgets(){//retrieves information from each widget and creates a Medicine object
        EditText name = findViewById(R.id.text_name_med);
        RadioGroup courseType = findViewById(R.id.radio_group);
        CheckBox morning = findViewById(R.id.morning_check);
        CheckBox day = findViewById(R.id.lunch_check);
        CheckBox evening = findViewById(R.id.evening_check);
        EditText quantity = findViewById(R.id.quantity_med);
        TextView date2 = findViewById(R.id.date);

        String nameM = name.getText().toString();
        int choice = courseType.getCheckedRadioButtonId();
        RadioButton checked = findViewById(choice);
        String type = checked.getText().toString();
        boolean cbm = morning.isChecked();
        boolean cbd = day.isChecked();
        boolean cbe = evening.isChecked();
        String howMany = quantity.getText().toString();
        int number = Integer.parseInt(howMany);

        MainList.lastId++;
        Medicine newMedicine = new Medicine(nameM, type, cbm, cbd, cbe, number, MainList.lastId, date2.getText().toString());
        writeNewMed(newMedicine.toString());//appends new information to the file "memory"
        Toast.makeText(CreateMedicine.this, "IŠSAUGOTA", Toast.LENGTH_LONG).show();//shows a toast message to inform the user about saving the application
    }

    void writeNewMed(String medInfo){//appends to "memory" file
        String names = "";

        try {
//This code was retrieved and modified from  https://www.journaldev.com/9383/android-internal-storage-example-tutorial
            //reads from the file and converts it into one string
            FileInputStream fin = openFileInput("memory");
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }

            fin.close();
//ends
            names += temp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //adds new information to the current one
        names += medInfo;


        try {
//This code was retrieved and modified from  https://www.journaldev.com/9383/android-internal-storage-example-tutorial
            //writes into the file, overwriting the past information
            FileOutputStream outputStream = openFileOutput("memory", Context.MODE_PRIVATE);
            outputStream.write(names.getBytes());

            outputStream.close();
//ends
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String errors = "";

    public int chekForPossibleErrors(){//checks if the user has entered the information correctly
        int possibleErrors = 0;
        boolean noEInR = false;
        EditText name = findViewById(R.id.text_name_med);
        RadioGroup courseType = findViewById(R.id.radio_group);
        CheckBox morning = findViewById(R.id.morning_check);
        CheckBox day = findViewById(R.id.lunch_check);
        CheckBox evening = findViewById(R.id.evening_check);
        EditText quantity = findViewById(R.id.quantity_med);

        String nameM = name.getText().toString();
        errors = "";
        if (nameM.equals("")){//cheks if a name is entered
            errors += "Įveskite pavadinimą" + "\n";
            possibleErrors++;
        }
        for (int i = 0; nameM.length()>i; i++){//checks if the entered name has characters , or \ which would prevent data reading
            char temp = nameM.charAt(i);
            if (temp==','||temp=='/'){
                errors += "Pavadinime negali būti simbolių , arba /"+ "\n";
                possibleErrors++;
                break;
            }
        }
        int choice = courseType.getCheckedRadioButtonId();
        if (choice==-1){//checks if nether radio button has been checked
            errors += "Pasirinkite kurso tipą"+ "\n";
            possibleErrors++;
        }
        else{
            noEInR = true;
        }

        boolean cbm = morning.isChecked();
        boolean cbd = day.isChecked();
        boolean cbe = evening.isChecked();
        if (cbm==false && cbd==false && cbe==false){//checks if every no box has been checked
            errors += "Pasirinkite kada gersite vaistus"+ "\n";
            possibleErrors++;
        }
        String howMany = quantity.getText().toString();
        if (howMany.equals("")){//checks if a number of medicine has been entered
            errors += "Įveskite tablečių skaičių" + "\n";
            possibleErrors++;
        }
        else {
            int number = Integer.parseInt(howMany);

            if (number > 500) {//checks if the number of tablets is bigger than 500
                errors += "Per didelis tablečių skaičius" + "\n";
                possibleErrors++;
            }
            if (number > 42 && noEInR==true) {//checks if the tablet number ir bigger than 42 and a course type has been chosen
                RadioButton checked = findViewById(choice);
                String type = checked.getText().toString();
                if (type.equals("Specialus")) {//checks if the special course type has been chosen
                    errors += "Kai kurso tipas specialus, tablečių kiekis negali būti didesnis nei 42" + "\n";
                    possibleErrors++;
                }
            }
        }
        return possibleErrors;
    }

}