package com.appsnipp.e4solutions.Models;

import java.io.Serializable;

public class VisitorProfile implements Serializable {

    public Data data;

    public class Data{
        public Visitor visitor;

        public class Visitor {
            public String id;
            public String uuid;
            public String visitorid;
            public String surname;
            public String firstname;
            public String primaryphone;
            public boolean is_staff;
            public boolean is_contractor;
            public boolean is_notify_on_visit;
            public String dateofbirth;
            public boolean banned;
            public String ban_reason = null;
            public String suburb;
            public String state;
            public float idimage_id;
            public String face_uuid = null;
            public float vaccination;
            public String company_id = null;
            public String postcode;
            public String address_1;
            public String address_2 = null;
            public String created_at;
            public String updated_at;
            public String distance = null;
            public String __typename;
            public String company = null;
            public VisitorLog visitorLog;

            public class VisitorLog{
                public String id;
                public String maskflag = null;
                public String detectid = null;
                public float deviceid;
                public String surname = null;
                public String firstname = null;
                public String primaryphone = null;
                public String vaccination = null;
                public String profileid = null;
                public float kiosk_group_id;
                public String idtype = null;
                public String visitorid;
                public String visittype;
                public float is_processed;
                public String temperature = null;
                public boolean contractor;
                public String guestmember = null;
                public String visitdatetime;
                public String leavedatetime = null;
                public String error_code = null;
                public String failure_reason = null;
                public String created_at;
                public boolean is_checkedintosnsw;
                public String __typename;
            }

            @Override
            public String toString() {
                return "Visitor{" +
                        "id='" + id + '\'' +
                        ", uuid='" + uuid + '\'' +
                        ", visitorid='" + visitorid + '\'' +
                        ", surname='" + surname + '\'' +
                        ", firstname='" + firstname + '\'' +
                        ", primaryphone='" + primaryphone + '\'' +
                        ", is_staff=" + is_staff +
                        ", is_contractor=" + is_contractor +
                        ", is_notify_on_visit=" + is_notify_on_visit +
                        ", banned=" + banned +
                        ", ban_reason='" + ban_reason + '\'' +
                        ", suburb='" + suburb + '\'' +
                        ", state='" + state + '\'' +
                        ", idimage_id=" + idimage_id +
                        ", face_uuid='" + face_uuid + '\'' +
                        ", vaccination=" + vaccination +
                        ", company_id='" + company_id + '\'' +
                        ", postcode='" + postcode + '\'' +
                        ", address_1='" + address_1 + '\'' +
                        ", address_2='" + address_2 + '\'' +
                        ", created_at='" + created_at + '\'' +
                        ", updated_at='" + updated_at + '\'' +
                        ", distance='" + distance + '\'' +
                        ", __typename='" + __typename + '\'' +
                        ", company='" + company + '\'' +
                        ", visitorLog=" + visitorLog +
                        '}';
            }
        }
    }

}
