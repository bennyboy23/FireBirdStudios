package uk.co.firebirdstudios.firebirdstudios;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class FragmentBookAStudio extends Fragment implements View.OnClickListener {
    private MimeMessage booking;
    private Gmail gmail;
    public static final String PREFS_NAME ="myPrefsFile";
    public FragmentBookAStudio() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_a_studio, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Button time = (Button) v.findViewById(R.id.time);
        TextView name = (TextView)v.findViewById(R.id.name);
               name.setText(settings.getString("Name", ""));
        TextView emailAddress = (TextView)v.findViewById(R.id.email);
        emailAddress.setText(settings.getString("Email",""));
        time.setOnClickListener(this);
        Button date = (Button) v.findViewById(R.id.date);
        date.setOnClickListener(this);
        Button confirm = (Button) v.findViewById(R.id.Confirm);
        confirm.setOnClickListener(this);
        //Toast.makeText(getActivity().getApplicationContext(),settings.getString("personId",""),Toast.LENGTH_LONG).show();
        return v;

    }

    public void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.time:
                showTimePickerDialog();
                break;
            case R.id.date:
                showDatePickerDialog();
                break;
            case R.id.Confirm:

                SendMail mSendMail = new SendMail();
                mSendMail.execute();
                break;
        }

    }

    private class SendMail extends AsyncTask<Void, Void, Void> {
        EditText emailEdit = (EditText) getActivity().findViewById(R.id.email);
        EditText nameEdit = (EditText) getActivity().findViewById(R.id.name);
        EditText bandNameEdit = (EditText) getActivity().findViewById(R.id.band_name);
        EditText telephoneEdit = (EditText) getActivity().findViewById(R.id.telephone);

        String email = emailEdit.getText().toString();
        String message = nameEdit.getText().toString() + "\n" + telephoneEdit.getText().toString() + "\n" + bandNameEdit.getText().toString();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                booking = createEmail("admin@firebirdstudios.co.uk", email, "booking", message);
                sendMessage(gmail, "me", booking);
                Toast.makeText(getActivity().getApplicationContext(), message + "\n" + email, Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static void sendMessage(Gmail service, String userId, MimeMessage email) throws IOException, MessagingException {
        Message message = createMessageWithEmail(email);
        message = service.users().messages().send(userId, message).execute();

    }

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress internetAddressTo = new InternetAddress(to);
        InternetAddress internetAddressFrom = new InternetAddress(from);

        email.setFrom(internetAddressFrom);
        email.addRecipient(MimeMessage.RecipientType.TO, internetAddressTo);
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public static com.google.api.services.gmail.model.Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        com.google.api.services.gmail.model.Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Button time = (Button) getActivity().findViewById(R.id.time);
            String displayTime = String.format("%02d:%02d", hourOfDay, minute);
            time.setText(displayTime);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Bundle userDate = new Bundle();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
            datePicker.getDatePicker().setMinDate(year);
            return datePicker;


        }

        public void onDateSet(DatePicker view, int year, int month, int day) {


            Button date = (Button) getActivity().findViewById(R.id.date);
            String displayDate = day + "/" + (month + 1) + "/" + year;
            date.setText(displayDate);

        }
    }


}
