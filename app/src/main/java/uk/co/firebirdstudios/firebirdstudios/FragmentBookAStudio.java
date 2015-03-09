package uk.co.firebirdstudios.firebirdstudios;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Benjy on 23/12/14.
 */
public class FragmentBookAStudio extends Fragment implements View.OnClickListener {
    private static final String username = "firebirdstudios7@gmail.com";
    private static final String password = "hellowor";

    public FragmentBookAStudio() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_a_studio, container, false);

        Button time = (Button) v.findViewById(R.id.time);
        time.setOnClickListener(this);
        Button date = (Button) v.findViewById(R.id.date);
        date.setOnClickListener(this);
        Button confirm = (Button) v.findViewById(R.id.Confirm);
        confirm.setOnClickListener(this);
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

                EditText emailEdit = (EditText) getActivity().findViewById(R.id.email);
                EditText nameEdit = (EditText) getActivity().findViewById(R.id.name);
                EditText bandNameEdit = (EditText) getActivity().findViewById(R.id.band_name);
                EditText telephoneEdit = (EditText) getActivity().findViewById(R.id.telephone);

                String email = emailEdit.getText().toString();
                String message = nameEdit.getText().toString() + "\n" + telephoneEdit.getText().toString() + "\n" + bandNameEdit.getText().toString();
                Toast.makeText(getActivity().getApplicationContext(), message + "\n" + email, Toast.LENGTH_SHORT).show();
                sendMail(email, "booking", message);
                break;
        }

    }

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Session createSessionObject() {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "487");

        return Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(email));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("admin@firebirdstudios.co.uk", "Firebird Studios"));
        message.setSubject(subject);
        message.setText(messageBody);
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

    private class SendMailTask extends AsyncTask<Message, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
