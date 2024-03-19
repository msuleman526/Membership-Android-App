package com.appsnipp.e4solutions.Models.Responses;

import java.io.Serializable;

public class MessageResponse implements Serializable {
    public Data data;

    public class Data{
        public PostMessage postMessage;

        public class PostMessage{
            public String ResponseMetadata;
        }
    }
}
