package com.CRM.constant;

public class Enpoint {

    public static final String API_PREFIX = "/crm/api/v1";

    public static final class Role {
        public static final String BASE = API_PREFIX + "/roles";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/detail/{id}";
        public static final String DELETE = "/detail/{id}";
    }

    public static final class Category {
        public static final String BASE = API_PREFIX + "/categories";
        public static final String CREATE = "/new";
    }

    public static final class Brand {
        public static final String BASE = API_PREFIX + "/brands";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
    }

    public static final class Banner {
        public static final String BASE = API_PREFIX + "/banners";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";

    }

    public static final class Product {
        public static final String BASE = API_PREFIX + "/products";
        public static final String ID = "/detail/{id}";
    }

}
