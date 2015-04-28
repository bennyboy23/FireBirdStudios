package uk.co.firebirdstudios.firebirdstudios;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChecker {
    /*
    this class performs the connection checks to see if the user is connected
    to the internet
     */
    private Context context;
    public ConnectionChecker(Context context){
        this.context = context;
    }
    public boolean isConnected(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            /*here we gather all the information on the network to determine
             if the user is connected to the internet

             */
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if(networkInfos != null){
                for (int i = 0; i <networkInfos.length;i++){
                    if (networkInfos[i].getState()== NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
