package com.CRM.constant;

public class Endpoint {

    public static final String API_PREFIX = "/crm/api/v1";

    public static final class Role {
        public static final String BASE = API_PREFIX + "/roles";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/detail/{id}";
        public static final String DELETE = "/detail/{id}";
        public static final String TRASH = "/trash";
        public static final String DELETE_MANY = TRASH + "/delete-many";
        public static final String RESTORE = TRASH + "/{id}";
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
        public static final String TRASH = "/trash";
        public static final String DELETE_MANY = TRASH + "/delete-many";
        public static final String RESTORE = TRASH + "/{id}";
    }

    public static final class Banner {
        public static final String BASE = API_PREFIX + "/banners";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";

    }

    public static final class Warehouse {
        public static final String BASE = API_PREFIX + "/warehouses";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";
    }

    public static final class Inventory {
        public static final String BASE = API_PREFIX + "/inventories";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";

        public static final String PRODUCT_WAREHOUSE = "/warehouses/{id}";
        public static final String PRODUCT_STORE = "/stores/{id}";

        public static final String TRANSACTION = "/transactions";
        public static final String TRANSACTION_WAREHOUSE = "/transactions/warehouse";
        public static final String ADJUST = "/warehouse/{id}/adjust";

    }

    public static final class Voucher {
        public static final String BASE = API_PREFIX + "/vouchers";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";
    }

        public static final class Supplier {
        public static final String BASE = API_PREFIX + "/suppliers";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";
    }

    public static final class PurchaseOrder {
        public static final String BASE = API_PREFIX + "/purchase-orders";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";
        public static final String TRASH_ID = TRASH + "/detail/{id}";

        public static final String CONFIRM = "/{id}/confirm";
        public static final String RECEIVE = "/receive/{code}";

    }
    
    public static final class Product {
        public static final String BASE = API_PREFIX + "/products";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String TRASH = "/trash";
        public static final String RESTORE = TRASH + "/{id}";
        public static final String TRASH_ID = TRASH + "/detail/{id}";
    }

    public static final class TransferTicket {
        public static final String BASE = API_PREFIX + "/transfer-tickets";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String CONFIRM = "/{id}/confirm";
        public static final String MARK = "/{ticketCode}/mark";
        public static final String STORE_RECEIVED = "/store/{ticketCode}/received";
        public static final String INFOR_RECEIPT = "/store/{ticketCode}";
    }

    public static final class Store {
        public static final String BASE = API_PREFIX + "/stores";
        public static final String ID = "/detail/{id}";
        public static final String CREATE = "/new";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        
    }
}
