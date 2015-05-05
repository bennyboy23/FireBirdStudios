package uk.co.firebirdstudios.firebirdstudios;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
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
    String message;

    AuthPreferences authPreferences = null;
    ConnectionChecker connectionChecker;
    private static int timePickerInput;
    public static final String PREFS_NAME = "myPrefsFile";

    public FragmentBookAStudio() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        authPreferences = new AuthPreferences(getActivity().getApplicationContext());
        connectionChecker = new ConnectionChecker(getActivity().getApplicationContext());
        View v = inflater.inflate(R.layout.fragment_book_a_studio, container, false);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Button time = (Button) v.findViewById(R.id.time);
        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(settings.getString("Name", ""));
        TextView emailAddress = (TextView) v.findViewById(R.id.email);
        emailAddress.setText(authPreferences.getUser());
        time.setOnClickListener(this);
        Button date = (Button) v.findViewById(R.id.date);
        date.setOnClickListener(this);
        Button confirm = (Button) v.findViewById(R.id.Confirm);
        confirm.setOnClickListener(this);
        Button equipment = (Button) v.findViewById(R.id.equipment_choice);
        equipment.setOnClickListener(this);
        Button endTime = (Button) v.findViewById(R.id.endTime);
        endTime.setOnClickListener(this);

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
        Context context = getActivity().getApplicationContext();
        switch (v.getId()) {
            case R.id.time:
                timePickerInput = v.getId();
                showTimePickerDialog();
                break;
            case R.id.endTime:
                timePickerInput = v.getId();
                showTimePickerDialog();
                break;
            case R.id.date:
                showDatePickerDialog();
                break;
            case R.id.Confirm:

                EditText nameEdit = (EditText) getActivity().findViewById(R.id.name);
                EditText bandNameEdit = (EditText) getActivity().findViewById(R.id.band_name);
                EditText telephoneEdit = (EditText) getActivity().findViewById(R.id.telephone);
                Button endTime = (Button) getActivity().findViewById(R.id.endTime);
                Button startTime = (Button) getActivity().findViewById(R.id.time);
                Button mDate = (Button)getActivity().findViewById(R.id.date);
                String date = mDate.getText().toString();
                String time = startTime.getText().toString() + " - " + endTime.getText().toString();
                String name = nameEdit.getText().toString();
                String bandName = bandNameEdit.getText().toString();

                String phoneNumber = telephoneEdit.getText().toString();
                String equipmentSelected = authPreferences.getEquipment();
                message = name + "\n" + phoneNumber + "\n" + bandName +
                        "\n" + "\n" + date + "\n" + time + "\n" + equipmentSelected;


                final SendMail mSendMail;
                mSendMail = new SendMail();
                int inputCheck = mSendMail.inputCheck();
                boolean connection = connectionChecker.isConnected();
                if (connection) {
                    if (inputCheck == 1) {
                        Toast.makeText(context, R.string.enter_name, Toast.LENGTH_SHORT).show();

                    } else if (inputCheck == 2) {
                        Toast.makeText(context, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    } else if (inputCheck == 3) {
                        Toast.makeText(context, R.string.enter_number, Toast.LENGTH_SHORT).show();
                    } else if (inputCheck == 4){
                        Toast.makeText(context,getString(R.string.check_time),Toast.LENGTH_SHORT).show();
                    }else if(inputCheck == 5){
                        Toast.makeText(context,getString(R.string.check_date),Toast.LENGTH_SHORT).show();
                    }
                    else if (inputCheck == 6) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                        alertBuilder.setTitle("Booking Confirmation");
                        alertBuilder.setMessage(message);
                        alertBuilder.setPositiveButton(getString(R.string.Confirm_Button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSendMail.execute();
                            }
                        });
                        alertBuilder.setNegativeButton(getString(R.string.Cancel_Button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertBuilder.show();

                    }
                } else {
                    Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.equipment_choice:
                DialogFragment dialog = new EquipmentDialogFragment();
                dialog.show(getChildFragmentManager(), "equipment");
                break;
        }


    }


    private class SendMail extends AsyncTask<Void, Void, Void> {
        //Here we gather the information to be sent to Firebird Studios
        EditText emailEdit = (EditText) getActivity().findViewById(R.id.email);
        EditText nameEdit = (EditText) getActivity().findViewById(R.id.name);
        EditText bandNameEdit = (EditText) getActivity().findViewById(R.id.band_name);
        EditText telephoneEdit = (EditText) getActivity().findViewById(R.id.telephone);
        Button endTime = (Button) getActivity().findViewById(R.id.endTime);
        Button startTime = (Button) getActivity().findViewById(R.id.time);
        Button mDate = (Button)getActivity().findViewById(R.id.date);
        String date = mDate.getText().toString();
        String name = nameEdit.getText().toString();
        String from = emailEdit.getText().toString();
        String phoneNumber = telephoneEdit.getText().toString();
        @Override
        protected Void doInBackground(Void... params) {
            try {

                //Here we send the email via the background thread
                Message email = createEmail("cricketgenius.evans@gmail.com", from, "Booking", message);

                sendMessage(email);

            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        public int inputCheck() {
            if (name.equals("")) {
                return 1;
            } else if (!from.contains("@")) {
                return 2;
            } else if (!(phoneNumber.length() == 11)) {
                return 3;
            } else if(startTime.getText().toString().equals(getString(R.string.pick_a_time))||
                    endTime.getText().toString().equals(getString(R.string.end_time))){
                return 4;
            }else if(date.isEmpty()){
                return 5;
            }else
                return 6;
        }

    }


    public void sendMessage(Message email) throws IOException, MessagingException {
        String url = "https://content.googleapis.com/gmail/v1/users/me/messages/send";

        url += "?access_token=" + URLEncoder.encode(authPreferences.getToken());

        String raw = email.toString();
        raw = raw.substring(5, raw.length() - 1);

        String json = "{\"raw\":\"" + raw + "\"}";

        Log.d("Email", raw);
        POST(url, json);


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
            // make POST request to the given URL
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
    protected String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;

    }

    /*
    the createEmail method and CreateMessageWithEmail method are both created by following the
    tutorial on the Gmail API site found at
    https://developers.google.com/gmail/api/guides/sending
     */
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

    /*
        the date and time picker Dialogs are inbuilt into the android system
        I created them by following the Android tutorial found at
        http://developer.android.com/guide/topics/ui/controls/pickers.html
         */
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
            switch (timePickerInput) {

                case R.id.time:
                    Button time = (Button) getActivity().findViewById(R.id.time);
                    String displayTime = String.format("%02d:%02d", hourOfDay, minute);
                    time.setText(displayTime);
                    break;
                case R.id.endTime:
                    Button endTime = (Button) getActivity().findViewById(R.id.endTime);
                    displayTime = String.format("%02d:%02d", hourOfDay, minute);
                    endTime.setText(displayTime);
                    break;
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @NonNull
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
