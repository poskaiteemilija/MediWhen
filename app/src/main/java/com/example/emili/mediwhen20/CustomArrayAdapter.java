package com.example.emili.mediwhen20;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by emili on 2019-03-11.
 */
//The following code was retrieved and modified from https://www.youtube.com/watch?v=lJxACtOZHnw
//this class is needed because each item in the TodayMed activity list has multiple TextViews and an ImageView
public class CustomArrayAdapter extends ArrayAdapter {
    Activity c;
    ArrayList<String> n, t, r, i = new ArrayList <String>();
    LayoutInflater layoutInflater;

    public View getView(int position, View cView, ViewGroup parent){//changes the values of the item UI elements
        View v = cView;
        if (v==null){
            v = layoutInflater.inflate(R.layout.today_med_layout, parent, false);
        }

        TextView name = v.findViewById(R.id.today_med_name);
        TextView tab = v.findViewById(R.id.how_many_tabs_left_today);
        TextView when = v.findViewById(R.id.when_to_take_today);
        ImageView status = v.findViewById(R.id.status_ic);

        name.setText(n.get(position));
        tab.setText(t.get(position));
        when.setText(r.get(position));
        //the three following statements change the status icon - if the drugs should be taken it is green, if the course is paused it is yellow and if the treatment has ended it is red
        if (i.get(position).equals("green")){
            status.setImageResource(R.drawable.go_ic);//sets icon which has been downloaded from a royalty free website, Icon made by RoudndIcons (https://www.flaticon.com/authors/roundicons) from www.flaticon.com
        }
        else if (i.get(position).equals("yellow")){
            status.setImageResource(R.drawable.pause_ic);//sets icon which has been downloaded from a royalty free website, Icon made by Maxim Basinski (https://www.flaticon.com/authors/maxim-basinski) from www.flaticon.com
        }
        else if (i.get(position).equals("red")){
            status.setImageResource(R.drawable.done_ic);//sets icon which has been downloaded from a royalty free website, Icon made by Maxim Basinski (https://www.flaticon.com/authors/maxim-basinski) from www.flaticon.com
        }
        return v;
    }


    public CustomArrayAdapter(@NonNull Activity activity, ArrayList<String> names, ArrayList<String> tabs, ArrayList<String> routs, ArrayList <String> images) {//constructor of the CustomArrayAdapter
        super(activity, R.layout.today_med_layout, names);
        c = activity;
        n = names;
        t = tabs;
        r = routs;
        i = images;

        layoutInflater = activity.getLayoutInflater();
    }
}
//ends