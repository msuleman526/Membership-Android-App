package com.appsnipp.e4solutions.Models;

import java.io.Serializable;

public class OTPResponse implements Serializable {
    public Data data;

    public class Data {
        public VerifyMfa verifyMfa;

    }

    public class VerifyMfa{
        public String mfa;
    }

}
