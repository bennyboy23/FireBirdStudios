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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class FragmentBookAStudio extends Fragment implements View.OnClickListener {
    private MimeMessage email;
    private Gmail gmail;

    AuthPreferences authPreferences = null;



    public static final String PREFS_NAME = "myPrefsFile";

    public FragmentBookAStudio() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        authPreferences = new AuthPreferences(getActivity());

        View v = inflater.inflate(R.layout.fragment_book_a_studio, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Button time = (Button) v.findViewById(R.id.time);
        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(settings.getString("Name", ""));
        TextView emailAddress = (TextView) v.findViewById(R.id.email);
        emailAddress.setText(settings.getString("Email", ""));
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

        String to = emailEdit.getText().toString();
        String message = nameEdit.getText().toString() + "\n" + telephoneEdit.getText().toString() + "\n" + bandNameEdit.getText().toString();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Message email = createEmail("admin@firebirdstudios.co.uk", to, "Booking", message);
                //Toast.makeText(getActivity().getApplicationContext(), message + "\n" + email, Toast.LENGTH_SHORT).show();
                sendMessage("me", email);

            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    public void sendMessage(String userId, Message email) throws IOException, MessagingException {
        String url = "https://content.googleapis.com/gmail/v1/users/me/messages/send";

        url += "?access_token=" + URLEncoder.encode(authPreferences.getToken());

        String raw = email.toString();
        raw = raw.substring(5, raw.length()-1);

        String json = "{\"raw\":\"" + raw + "\"}";

        Log.d("Email", raw);

        Log.d("url", url);
        String response = POST(url, json);
        Log.d("Twat", response);

    }

    public String POST(String url, String json) {
        InputStream inputStream = null;
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(json));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setHeader(HTTP.CONTENT_TYPE,
                "application/json;charset=UTF-8");
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
            Log.d("Response Code", Integer.toString(httpResponse.getStatusLine().getStatusCode()));
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }

    // The same helper function that convert the response buffer into string
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }

    public static Message createEmail(String to, String from, String subject, String bodyText) throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress internetAddressTo = new InternetAddress(to);
        InternetAddress internetAddressFrom = new InternetAddress(from);

        email.setFrom(internetAddressFrom);
        email.addRecipient(MimeMessage.RecipientType.TO, internetAddressTo);
        email.setSubject(subject);
        email.setText(bodyText);
        return createMessageWithEmail(email);
    }

    public static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
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
