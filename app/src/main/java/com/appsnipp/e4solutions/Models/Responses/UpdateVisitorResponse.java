package com.appsnipp.e4solutions.Models.Responses;

import com.appsnipp.e4solutions.Models.VisitorEdge;
import com.appsnipp.e4solutions.Models.VisitorProfile;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateVisitorResponse implements Serializable {

    public Data data;

    public class Data{

        public VisitorUpdate visitorUpdate;

        public class VisitorUpdate{
            public VisitorProfile.Data.Visitor visitor;
            public String __typename;
        }
    }
}
