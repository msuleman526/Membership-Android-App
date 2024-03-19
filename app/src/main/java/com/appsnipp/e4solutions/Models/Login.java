package com.appsnipp.e4solutions.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Login implements Serializable {
    public ArrayList<Error> errors = new ArrayList<Error>();
    public Data data;

    @Override
    public String toString() {
        return "Login{" +
                "errors=" + errors +
                ", data=" + data +
                '}';
    }

    public class Error {
        public String message;
    }

    public class Data {
        public Token tokenAuth;

    }

    public class Token{
        public String token;
        public User user;
        public String __typename;

        public class User {
            public String id;
            public String firstName;
            public String lastName;
            public String email;
            public String isMFA = "";
            public String phone;
            public String birthday = null;
            public String address;
            public String username;
            public boolean isActive;
            public boolean isAdmin;
            public boolean isOffsite;
            public String lastLogin;
            public String dateJoined;
            public Organization organization;
            public String __typename;

            public class Organization {
                public String id;
                public String name;
                public String logo;
                public String slug;
                public String modified;
                public String offsiteApi;
                public String onsiteApi;
                public String phone;
                public String website;
                public String vaccinationTheme = null;
                public String membershipType = null;
                public Theme theme;
                public ArrayList<Tier> tiers = new ArrayList<Tier>();
                public ArrayList<App> apps = new ArrayList<App>();
                public String __typename;

                @Override
                public String toString() {
                    return "Organization{" +
                            "id='" + id + '\'' +
                            ", name='" + name + '\'' +
                            ", logo='" + logo + '\'' +
                            ", slug='" + slug + '\'' +
                            ", modified='" + modified + '\'' +
                            ", offsiteApi='" + offsiteApi + '\'' +
                            ", onsiteApi='" + onsiteApi + '\'' +
                            ", phone='" + phone + '\'' +
                            ", website='" + website + '\'' +
                            ", vaccinationTheme='" + vaccinationTheme + '\'' +
                            ", theme=" + theme +
                            ", tiers=" + tiers +
                            ", apps=" + apps +
                            ", __typename='" + __typename + '\'' +
                            '}';
                }

                public class Tier {
                    public String name;
                    public String code;
                    public String value;
                    public String primaryColor;
                    public String secondaryColor;
                    public String __typename;

                    @Override
                    public String toString() {
                        return "Tier{" +
                                "name='" + name + '\'' +
                                ", code='" + code + '\'' +
                                ", value='" + value + '\'' +
                                ", primaryColor='" + primaryColor + '\'' +
                                ", secondaryColor='" + secondaryColor + '\'' +
                                ", __typename='" + __typename + '\'' +
                                '}';
                    }
                }

                public class App {
                    public String name;
                    public String slug;
                    public String __typename;
                }

                public class Theme {
                    public String primaryColor;
                    public String secondaryColor;
                    public String __typename;

                }
            }
        }
    }

}
