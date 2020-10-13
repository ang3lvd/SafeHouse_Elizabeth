package com.divinesecurity.safehouse.listViewAdapterPackage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.divinesecurity.safehouse.R;
import com.divinesecurity.safehouse.registerPackage.RegisterActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Spinner_Adapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> companies;
    private ArrayList<String> images;
    private ArrayList<String> states;
    private LayoutInflater inflter;

    private String beforeState;

    private Dialog dialog;

    /*AlertDialog.Builder builder;
    AlertDialog alertDialog;*/

    private RegisterActivity registerActivity;

    public Spinner_Adapter(Context c, RegisterActivity rAct, ArrayList<String>  companies, ArrayList<String>  images, ArrayList<String> states){

        this.context = c;
        this.companies = companies;
        this.images = images;
        this.states = states;

        this.registerActivity = rAct;

        inflter = (LayoutInflater.from(c));

        beforeState = "Companies List";
    }

    @Override
    public int getCount() {
        return companies.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.spinner_items, null);

        TextView t1 = view.findViewById(R.id.idcompany_name);
        CircleImageView v1 = view.findViewById(R.id.idcompany_logo);
        TextView t4 = view.findViewById(R.id.idcompany_info);

        t4.setVisibility(View.GONE);

        t1.setText(companies.get(i));
        if (!companies.get(i).equals("-select company-")) {
            Picasso.with(context).load("https://divinesecurityapp.com/resources/images/profiles/companies/"+ images.get(i))
                    .fit().centerCrop()
                    .placeholder(R.drawable.spinner_progress_animation)
                    .error(R.drawable.icons8_shield_64)
                    .into(v1);

            /*if(!states.get(i).equals(beforeState)){
                TextView t3 = view.findViewById(R.id.idstate);
                t3.setText(states.get(i));
                t3.setVisibility(View.VISIBLE);
            }*/

            beforeState = companies.get(i);
        } else{
            v1.setVisibility(View.GONE);
        }
        return  view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.spinner_items, null);

        TextView t1 = convertView.findViewById(R.id.idcompany_name);
        CircleImageView v1 = convertView.findViewById(R.id.idcompany_logo);
        TextView t4 = convertView.findViewById(R.id.idcompany_info);

        t1.setText(companies.get(position));
        if (!companies.get(position).equals("-select company-")) {
            Picasso.with(context).load("https://divinesecurityapp.com/resources/images/profiles/companies/"+ images.get(position))
                    .fit().centerCrop()
                    .placeholder(R.drawable.spinner_progress_animation)
                    .error(R.drawable.icons8_shield_64)
                    .into(v1);

            if(!states.get(position).equals(beforeState)){
                TextView t3 = convertView.findViewById(R.id.idstate);
                t3.setText(states.get(position));
                t3.setVisibility(View.VISIBLE);
            }

            final int pos = position;
            t4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(context,companies.get(pos), Toast.LENGTH_SHORT).show();
                    //registerActivity.showCompanyInfo(companies.get(pos));

                    //dialog window
                    dialog = new Dialog(registerActivity);
                    //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_companyinfo);

                    final TextView txtname = dialog.findViewById(R.id.dialogCompanyText);
                    txtname.setText(companies.get(pos));

                    Button btncancel = dialog.findViewById(R.id.btn_UpdAccCANCEL);
                    btncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if (dialog.getWindow() != null)
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });


            beforeState = companies.get(position);
        } else{
            v1.setVisibility(View.GONE);
            t4.setVisibility(View.GONE);
        }
        return  convertView;
    }
}

