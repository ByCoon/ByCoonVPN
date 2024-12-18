/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core;

import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.LogItem;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.ConnectionStatus;



interface IStatusCallbacks {
    /**
     * Called when the service has a new status for you.
     */
    oneway void newLogItem(in LogItem item);

    oneway void updateStateString(in String state, in String msg, in int resid, in ConnectionStatus level, in Intent intent);

    oneway void updateByteCount(long inBytes, long outBytes);

    oneway void connectedVPN(String uuid);
}
