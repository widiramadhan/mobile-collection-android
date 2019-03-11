package sfi.mobile.collection.listener;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class MyPhoneStateListener extends PhoneStateListener {
    public int signalStrengthValue;

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (signalStrength.isGsm()) {
            if (signalStrength.getGsmSignalStrength() != 99)
                signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
            else
                signalStrengthValue = signalStrength.getGsmSignalStrength();
        } else {
            signalStrengthValue = signalStrength.getCdmaDbm();
        }
        //txtSignalStr.setText("Signal Strength : " + signalStrengthValue);
    }
}