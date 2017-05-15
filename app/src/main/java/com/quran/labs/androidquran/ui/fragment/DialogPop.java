package com.quran.labs.androidquran.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quran.labs.androidquran.R;


public class DialogPop extends DialogFragment {

  PopupDialog mPopupDialog;
  public DialogPop() {
    // Required empty public constructor
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.dialog)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
          }
        });
    builder.create();
    return mPopupDialog.createDialog();
  }



  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof PopupDialog) {
      mPopupDialog = (PopupDialog) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement PopupDialog");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  public interface PopupDialog{
    Dialog createDialog();
  }
}
