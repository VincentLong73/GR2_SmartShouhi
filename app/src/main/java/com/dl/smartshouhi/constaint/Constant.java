package com.dl.smartshouhi.constaint;

public class Constant {

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String SHARED_PREFS_API = "shared_prefs_api";

    // key for storing email.
    public static final String EMAIL_KEY = "email_key";
    public static final String USERNAME_KEY = "userName_key";
    public static final String FULLNAME_KEY = "fullName_key";
    public static final String PHONE_KEY = "phone_key";
    public static final String DOB_KEY = "dob_key";
    public static final String ISADMIN_KEY = "isAdmin_key";
    public static final String ID_KEY = "id_key";
    // key for storing password
    public static final String PASSWORD_KEY = "password_key";

    public static final String INVOICE_ITEMS = "invoice_items";

    //result get api
    public static final String RESULT_GET_API =  "result_api";

    private static final String PREFIX_URL = " http://192.168.1.2/do_an";
    //login
    public static final String URL_LOGIN = PREFIX_URL + "/login/login.php";
    public static final String URL_REGIS = PREFIX_URL + "/login/register.php";

    //user
    public static final String URL_CHANGE_PASSWORD = PREFIX_URL + "/user/update_password.php";
    public static final String URL_CHANGE_PROFILE = PREFIX_URL + "/user/update_profile_user.php";
    //invoice
    public static final String URL_GET_INVOICE_BY_USER_ID = PREFIX_URL + "/invoice/get_invoice_by_userId.php?id=";
    public static final String URL_GET_INVOICE_BY_USER_ID_YEAR = PREFIX_URL + "/invoice/get_invoice_by_userId_year.php?";
    public static final String URL_ADD_A_INVOICE = PREFIX_URL + "/invoice/add_invoice.php";
    public static final String URL_UPDATE_A_INVOICE = PREFIX_URL + "/invoice/update_invoice.php";
    public static final String URL_DELETE_A_INVOICE = PREFIX_URL + "/invoice/delete_invoice.php";

    //item
    public static final String URL_ADD_LIST_ITEM = PREFIX_URL + "/item/add_item.php";

    //admin
    public static final String URL_INFO_FOR_ADMIN = PREFIX_URL + "/admin/get_count_user_invoice_item.php";
    public static final String URL_GET_USER_NOT_ADMIN = PREFIX_URL + "/admin/get_list_user_not_admin.php";



}
