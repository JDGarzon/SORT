//
// Copyright (c) ZeroC, Inc. All rights reserved.
//

import com.zeroc.Ice.Current;

public final class CallbackReceiverI implements Demo.CallbackReceiver
{

    @Override
    public void callback(String response, Current current) {
        System.out.println(response);
    }
}
