package uk.co.firebirdstudios.firebirdstudios;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class FragmentContactUs extends Fragment implements View.OnClickListener {

    AuthPreferences authPreferences;
    ConnectionChecker connectionChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        connectionChecker = new ConnectionChecker(getActivity().getApplicationContext());
        authPreferences = new AuthPreferences(getActivity().getApplicationContext());
        TextView email = (TextView) v.findViewById(R.id.emailAddress);
        Button callUs = (Button) v.findViewById(R.id.phoneNumber);
        callUs.setOnClickListener(this);
        Button maps = (Button) v.findViewById(R.id.maps);
        Button facebookLink = (Button) v.findViewById(R.id.facebook_link);
        Button twitterLink = (Button) v.findViewById(R.id.twitter_link);
        Button send = (Button) v.findViewById(R.id.send);
        maps.setOnClickListener(this);
        facebookLink.setOnClickListener(this);
        twitterLink.setOnClickListener(this);
        send.setOnClickListener(this);
        if (authPreferences.isLoggedIn()) {
            email.setText(authPreferences.getUser());
        }
        return v;


    }

    @Override
    public void onClick(View v) {
        Context context = getActivity().getApplicationContext();
        Intent intent;
        if (v.getId() == R.id.phoneNumber) {
            callStudio();
        }
        if (v.getId() == R.id.maps) {
            showMaps();
        }
        if (v.getId() == R.id.facebook_link) {
            intent = openFacebook(context);
            startActivity(intent);
        }
        if (v.getId() == R.id.twitter_link) {
            try {
                context.getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2699425295"));
                startActivity(intent);
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/firebirdmusicuk"));
                startActivity(intent);
            }
        }
        if (v.getId() == R.id.send) {
            if (authPreferences.isLoggedIn()) {
                SendMail sendMail = new SendMail();
                switch (sendMail.inputCheck()) {
                    case 1:
                        Toast.makeText(context, getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(context, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        sendMail.execute();
                        break;
                }
            } else {
                if (connectionChecker.isConnected()) {
                    EditText userName = (EditText) getActivity().findViewById(R.id.contactName);
                    EditText userMessage = (EditText) getActivity().findViewById(R.id.message);
                    String name = userName.getText().toString();
                    String message = name + "\n" + userMessage.getText().toString();
                    Intent send = new Intent(Intent.ACTION_SENDTO);
                    String uriMail = "mailto:" + Uri.encode("cricketgenius.evans@gmail.com")
                            + "?subject=" + Uri.encode("Comments")
                            + "&body=" + Uri.encode(message);
                    Uri uri = Uri.parse(uriMail);
                    send.setData(uri);
                    startActivity(Intent.createChooser(send, context.getString(R.string.email_client)));
                } else {
                    Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    public void callStudio() {
        Uri phoneNumber = Uri.parse("tel:01179721830");
        PackageManager packageManager = getActivity().getPackageManager();

        Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneNumber);
        List<ResolveInfo> activities =
                packageManager.queryIntentActivities(callIntent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isSafe = activities.size() > 0;
        if (isSafe) {
            startActivity(callIntent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "please get a phone app", Toast.LENGTH_SHORT).show();

        }

    }

    public void showMaps() {

        Intent i = new Intent(Intent.ACTION_VIEW);

        Toast.makeText(getActivity().getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
        i.setData(Uri.parse("geo:51.432356,-2.540121?q=firebird+studios"));
        Intent chooser = Intent.createChooser(i, "Launch Maps");
        startActivity(chooser);
    }

    public static Intent openFacebook(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://page/128418193885308")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/FirebirdStudiosUK")); //catches and opens a url to the desired page
        }
    }

    private class SendMail extends AsyncTask<Void, Void, Void> {
        EditText userEmail = (EditText) getActivity().findViewById(R.id.emailAddress);
        EditText userName = (EditText) getActivity().findViewById(R.id.contactName);
        EditText userMessage = (EditText) getActivity().findViewById(R.id.message);
        String mEmail = userEmail.getText().toString();
        String name = userName.getText().toString();
        String message = name + "/n" + userMessage.getText().toString();

        @Override

        protected Void doInBackground(Void... params) {
            try {
                //Here we send the email via the background thread
                Message email = createEmail("cricketgenius.evans@gmail.com", mEmail, "Comment", message);

                sendMessage(email);

            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public int inputCheck() {
            if (name.equals("")) {
                return 1;
            } else if (!mEmail.contains("@")) {
                return 2;
            } else {
                return 3;
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
}

