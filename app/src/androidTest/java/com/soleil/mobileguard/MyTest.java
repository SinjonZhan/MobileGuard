package com.soleil.mobileguard;

import android.test.AndroidTestCase;

import com.soleil.mobileguard.utils.ServiceUtils;

import org.junit.Test;

public class MyTest extends AndroidTestCase {

    @Test
    public void TestService(){
        ServiceUtils.isServiceRunning(getContext(), "");
    }
}
