package com.eis.phoneringer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eis.phoneringer.structure.AppManager;
import com.eis.phoneringer.structure.ReceivedMessageListener;
import com.eis.phoneringer.structure.RingCommand;
import com.eis.smslibrary.SMSHandler;
import com.eis.smslibrary.SMSMessage;
import com.eis.smslibrary.SMSPeer;
import com.eis.smslibrary.exceptions.InvalidSMSMessageException;
import com.eis.smslibrary.exceptions.InvalidTelephoneNumberException;
import com.eis.smslibrary.listeners.SMSSentListener;

import it.lucacrema.preferences.PreferencesManager;

/**
 * Test app: through this MainActivity we can test out our basic library for sending ring commands
 *
 * @author Alberto Ursino
 */
public class MainActivity extends AppCompatActivity {

    private static Button ringButton = null;
    private static EditText phoneNumber = null;
    private static final String STRING_KEY_PASSWORD = "stringKeyPassTest";
    private static final String DEFAULT_PASSWORD = "_password";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = getApplicationContext();
        ringButton = findViewById(R.id.ring_button);
        phoneNumber = findViewById(R.id.phone_number);

        /**
         * Default password equals for all the devices
         */
        PreferencesManager.setString(context, STRING_KEY_PASSWORD, DEFAULT_PASSWORD);

        /**
         * Some tricks with permissions
         */
        if (checkPermission()) {
            SMSHandler.getInstance().setup(context);
            SMSHandler.getInstance().setReceivedListener(new ReceivedMessageListener(context));
        } else {
            requestPermission();
        }

        ringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(context);
            }
        });
    }

    /**
     * Sends a ring command to the phone number given by the user
     *
     * @param context The current application context
     */
    private void sendCommand(final Context context) {
        //Callback when the command is sent
        SMSSentListener smsSentListener = new SMSSentListener() {
            @Override
            public void onSMSSent(SMSMessage message, SMSMessage.SentState sentState) {
                Toast.makeText(context, "Command sent to " + phoneNumber.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        };
        //Passing to the AppManager a new RingCommand built through the phone Number the user given
        try {
            AppManager.getInstance().sendCommand(context, new RingCommand(new SMSPeer(phoneNumber.getText().toString()), PreferencesManager.getString(context, STRING_KEY_PASSWORD)), smsSentListener);
        } catch (InvalidTelephoneNumberException e) {
            Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show();
        } catch (InvalidSMSMessageException e) {
            Toast.makeText(context, "Invalid SMSMessage object", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return true if the app has permissions, false otherwise
     */
    public boolean checkPermission() {
        if (!(getApplicationContext().checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) ||
                !(getApplicationContext().checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED))
            return false;
        else
            return true;
    }

    /**
     * Checks if the user grant the SMS permissions
     */
    public void requestPermission() {
        if (!checkPermission())
            requestPermissions(new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            SMSHandler.getInstance().setup(getApplicationContext());
        } else {
            finish();
            System.exit(0);
        }
    }
}
