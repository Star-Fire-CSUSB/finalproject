package com.finalproject.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reminder#newInstance} factory method to
 * create an instance of this fragment.
 */

public class Reminder extends Fragment {
    //Database References
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference carRef = mDatabase.getReference();
    DatabaseReference userCarRef = carRef.child("user-cars").child(user);
    DatabaseReference specificCarRef;
    DatabaseReference mileageRef;

    int initialMileage = 0;
    String data;

    //    DatabaseReference mileageRef = uid.child();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Reminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reminder.
     */
    // TODO: Rename and change types and number of parameters
    public static Reminder newInstance(String param1, String param2) {
        Reminder fragment = new Reminder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);

        // Declare Buttons and TextViews elements from Reminders page
        final Button btnUpdateMileage = (Button)v.findViewById(R.id.btnUpdateMileage);
        final TextView currentMileage = (TextView)v.findViewById(R.id.textViewCurrentMileage);
        final TextView recentMaintenance = (TextView)v.findViewById(R.id.textViewRecentMain);
        final TextView upcomingMaintenance = (TextView)v.findViewById(R.id.textViewUpcomingMaintenance);

        // Allows textviews to be scrollable
        currentMileage.setMovementMethod(new ScrollingMovementMethod());
        recentMaintenance.setMovementMethod(new ScrollingMovementMethod());
        upcomingMaintenance.setMovementMethod((new ScrollingMovementMethod()));




        while(true) {

         userCarRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot ds : snapshot.getChildren()){
                     String uid = ds.getKey();
                     specificCarRef = userCarRef.child(uid);
                     mileageRef = specificCarRef.child("mileage");

                     mileageRef.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             data = snapshot.getValue().toString();
                             int dataInt = Integer.parseInt(data);
                             getCurrentMileage(currentMileage,dataInt);
                             recentMaintenance.setText(oilHealth(data) + "\n" + tireHealth(data) + "\n" +
                                     batteryHealth(data) + "\n" + brakeHealth(data));
                             maintenanceRoutine(initialMileage,dataInt,recentMaintenance,upcomingMaintenance);

                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });

                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

            // Controls what happens when you click on the Update Mileage Button
            btnUpdateMileage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pops Up a Window that asks to update mileage

                    Intent popUp = new Intent(getContext(), popActivity.class);
                    startActivity(popUp);


                }
            });

            return v;
        }

}

    // Methods used to display messages into the TextView objects
    public void getCurrentMileage(TextView currentMileage, int mileage){
        String theCurrentMileage = Integer.toString(mileage);
        currentMileage.setText(String.format("The current mileage on your vehicle is %s miles.",
                theCurrentMileage));
    }


    // Methods for upcoming maintenance schedule
    public String everyFiveThousand(int currentMileage){
        String nextUpcoming = Integer.toString(5000 - (currentMileage % 5000));
            return "Upcoming in " + nextUpcoming + " miles:\n" +
                    "• Change Oil and Filter\n" +
                    "• Rotate/Balance Tires\n" +
                    "• Inspect Battery/Clean contacts\n" +
                    "• Inspect Fluids\n" +
                    "• Inspect Hoses\n" +
                    "• Inspect Tire Inflation/Condition";
    }

    public String everyFifteenThousand(int currentMileage){
        String nextUpcoming = Integer.toString(15000 - (currentMileage % 15000));
        return "Upcoming in " + nextUpcoming + " miles:\n" +
                "• Wheel Alignment\n" +
                "• Replace Wiper Blades\n" +
                "• Replace Engine Air Filter\n" +
                "• Inspect Rotors and Pads";
    }

    public String everyThirtyThousand(int currentMileage){
        String nextUpcoming = Integer.toString(30000 - (currentMileage % 30000));

        return "Upcoming in " + nextUpcoming +  " miles:\n" +
                "• Replace Battery (if needed)\n" +
                "• Inspect/Replace Cabin Air Filter\n" +
                "• Inspect/ Replace Brake Fluid\n" +
                "• Change Brake Pads/Rotors (if needed)";
    }

    public String everySixtyThousand(int currentMileage){
        String nextUpcoming = Integer.toString(60000 - (currentMileage % 60000));
        return "Upcoming in " + nextUpcoming + " miles:\n" +
                "• Inspect Timing Belt\n" +
                "• Inspect/Replace Serpentine Belts\n" +
                "• Inspect Transmission Fluid";
    }

    public String everyHundredTwentyThousand(int currentMileage){
        String nextUpcoming = Integer.toString(120000 - (currentMileage % 120000));
        return "Upcoming in " + nextUpcoming + " miles:\n" +
                "• Change Timing Belt\n" +
                "• Change Spark Plugs\n" +
                "• Change Engine Coolant\n" +
                "• Change Transmission Fluid\n" +
                "• Change Power Steering Fluid";
    }



    public int maintenanceRoutine(int initialMileage, int currentMileage,
                                  TextView recentMaintenance, TextView upcomingMaintenance ) {


        if (currentMileage - initialMileage < 5000){
             upcomingMaintenance.setText(everyFiveThousand(currentMileage));
        }

        if (currentMileage - initialMileage >= 5000){
            upcomingMaintenance.setText(String.format("%s\n\n%s", everyFiveThousand(currentMileage),
                    everyFifteenThousand(currentMileage)));
        }

        if (currentMileage - initialMileage >= 15000){
            upcomingMaintenance.setText(String.format("%s\n\n%s", everyFiveThousand(currentMileage),
                    everyThirtyThousand(currentMileage)));
        }

        if (currentMileage - initialMileage >= 30000){
            upcomingMaintenance.setText(String.format("%s\n\n%s",
                    everyFiveThousand(currentMileage),
                    everySixtyThousand(currentMileage)));
        }

        if (currentMileage - initialMileage >= 60000){
            upcomingMaintenance.setText(String.format("%s\n\n%s",
                    everyFiveThousand(currentMileage),
                    everyHundredTwentyThousand(currentMileage)));
        }

        if (currentMileage - initialMileage >= 120000){
            upcomingMaintenance.setText(everyFiveThousand(currentMileage));
            initialMileage = currentMileage;
            return initialMileage;
        }

        return initialMileage;
    }

    public String oilHealth (String currentMileage){
        double milesUntilChange = 5000;
        double currentMileageInt = Integer.parseInt(currentMileage);
        double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100)) /100;
        NumberFormat format = NumberFormat.getPercentInstance(Locale.US);
        String percentage = format.format(percent);

        return "Oil Health: " + percentage;
    }

    public String tireHealth (String currentMileage){
        double milesUntilChange = 60000;
        double currentMileageInt = Integer.parseInt(currentMileage);
        double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100)) / 100;
        NumberFormat format = NumberFormat.getPercentInstance(Locale.US);
        String percentage = format.format(percent);

        return "Tire Health: " + percentage;
    }

    public String batteryHealth (String currentMileage){
        double milesUntilChange = 50000;
        double currentMileageInt = Integer.parseInt(currentMileage);
        double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100)) / 100;
        NumberFormat format = NumberFormat.getPercentInstance(Locale.US);
        String percentage = format.format(percent);

        return "Battery Health: " + percentage;
    }


    public String brakeHealth (String currentMileage) {
        double milesUntilChange = 50000;
        double currentMileageInt = Integer.parseInt(currentMileage);
        double percent = (100 - (((currentMileageInt % milesUntilChange) / milesUntilChange) * 100)) / 100;
        NumberFormat format = NumberFormat.getPercentInstance(Locale.US);
        String percentage = format.format(percent);

        return "Brake Health: " + percentage;
    }

}