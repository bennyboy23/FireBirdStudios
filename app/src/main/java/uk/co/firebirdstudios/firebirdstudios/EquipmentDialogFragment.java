package uk.co.firebirdstudios.firebirdstudios;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.ArrayList;


public class EquipmentDialogFragment extends DialogFragment {
    private ArrayList<Integer> selectedItemsIndex = new ArrayList<>();

    public interface equipmentDialogFragmentListener{
        //this interface is how the new dialog communicates with the host activity
        public void onConfirm(ArrayList<Integer> arrayList);

        public void onCancel();
    }

    equipmentDialogFragmentListener fragmentListener;




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentListener = (equipmentDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement equipmentDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        boolean[] isSelected = {false, false, false, false, false, false};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.equipment);
        builder.setMultiChoiceItems(R.array.equipment_hire, isSelected,
                new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                Toast.makeText(getActivity().getApplicationContext(),
                        which + " " + isChecked, Toast.LENGTH_LONG).show();
                if (isChecked) {
                    selectedItemsIndex.add(which);
                } else if (selectedItemsIndex.contains(which)) {
                    selectedItemsIndex.remove(Integer.valueOf(which));

                }
            }
        });
        builder.setNegativeButton(R.string.Cancel_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                fragmentListener.onCancel();
            }
        });
        builder.setPositiveButton(R.string.Confirm_Button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                fragmentListener.onConfirm(selectedItemsIndex);
            }
        });
        return builder.create();
    }


}
