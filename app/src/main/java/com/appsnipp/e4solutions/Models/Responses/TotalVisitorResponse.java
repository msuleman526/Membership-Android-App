package com.appsnipp.e4solutions.Models.Responses;

import com.appsnipp.e4solutions.Models.VisitorEdge;

import java.io.Serializable;
import java.util.ArrayList;

public class TotalVisitorResponse implements Serializable {

    public Data data;

    public class Data{

        public Visitors visitors;

        public class Visitors{
            public ArrayList<VisitorEdge> edges;
            public PageInfo pageInfo;
            public String totalCount;
            public String __typename;

            public class PageInfo{
                public boolean hasNextPage;
                public String cursor;
                public String __typename;
            }

        }
    }
}
