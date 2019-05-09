package me.timgu.flashmemorize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class NewDeckDialogueFragment extends DialogFragment {

    public interface NewDeckDialogueListener {

        public void onNewDeckDialogPositiveClick(DialogFragment dialog, String msg) throws IOException;
        public void onNewDeckDialogNegativeClick(DialogFragment dialog);
    }

    NewDeckDialogueListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewDeckDialogueListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = inflater.inflate(R.layout.fragment_new_deck_dialogue,null);

        builder.setView(dialogView)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText newName = dialogView.findViewById(R.id.flashcard_dialog_number);
                        String msg = newName.getText().toString();
                        try {
                            listener.onNewDeckDialogPositiveClick(NewDeckDialogueFragment.this,msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onNewDeckDialogNegativeClick(NewDeckDialogueFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
