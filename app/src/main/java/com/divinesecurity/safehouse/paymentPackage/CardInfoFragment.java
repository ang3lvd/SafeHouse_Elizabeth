package com.divinesecurity.safehouse.paymentPackage;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.craftman.cardform.Card;
import com.craftman.cardform.OnPayBtnClickListner;
import com.divinesecurity.safehouse.R;


public class CardInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View fragmentView = inflater.inflate(R.layout.fragment_card_info, container, false);

        final com.craftman.cardform.CardForm cardForm = fragmentView.findViewById(R.id.cardform);
        TextView txtDes = fragmentView.findViewById(R.id.payment_amount);
        Button btnPay = fragmentView.findViewById(R.id.btn_pay);

        txtDes.setText("$0.00");
        txtDes.setVisibility(View.INVISIBLE);

        btnPay.setText("Save Info");

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                if (card.validateCard()) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + card.getNumber() + "\n" +
                            "Card expiry date: " + card.getExpMonth() + "/" + card.getExpYear() + "\n" +
                            "Card CVV: " + card.getCVC() + "\n" +
                            "Card name: " + card.getName());
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast toast = Toast.makeText(getActivity(), "Thank you for purchase", Toast.LENGTH_LONG);
                            TextView v = toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.GREEN);
                            toast.show();


                        }
                    });

                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                }else {
                    Toast toast = Toast.makeText(getActivity(), "Please complete the form", Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);
                    toast.show();
                }
            }
        });


        // Inflate the layout for this fragment
        return fragmentView;
    }
}
