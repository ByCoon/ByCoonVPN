// StatusIPC.aidl
package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core;

// Declare any non-default types here with import statements
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.IStatusCallbacks;
import android.os.ParcelFileDescriptor;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.TrafficHistory;


interface IServiceStatus {
         /**
          * Registers to receive OpenVPN Status Updates and gets a
          * ParcelFileDescript back that contains the log up to that point
          */
         ParcelFileDescriptor registerStatusCallback(in IStatusCallbacks cb);

         /**
           * Remove a previously registered callback interface.
           */
        void unregisterStatusCallback(in IStatusCallbacks cb);

        /**
         * Returns the last connedcted VPN
         */
        String getLastConnectedVPN();

        /**
          * Sets a cached password
          */
       void setCachedPassword(in String uuid, int type, String password);

       /**
       * Gets the traffic history
       */
       TrafficHistory getTrafficHistory();
}
