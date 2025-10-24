package com.t4app.t4everandroid.network;

public class ApiConfig {
    public static final String BASE_URL = "https://backend.t4ever.com/";
    public static final String CLIENT_ID = "395482359077-q47be5ha9cp073r4qlpm48ip9ggm5rdc.apps.googleusercontent.com";

    public static final String LOGIN_URL = "api/auth/login";
    public static final String LOGOUT_URL = "api/auth/logout";
    public static final String REGISTER_URL = "api/auth/register";
    public static final String ASSISTANTS_URL = "api/assistants";
    public static final String GET_USER_INFO = "api/auth/user";
    public static final String ACTIONS_ASSISTANTS_URL = "api/assistants/{uuid}";
    public static final String GET_QUESTIONS_ASSISTANT = "api/assistant-questions";
    public static final String UPDATE_PROFILE_URL = "api/update-user-info";
    public static final String CHANGE_PASSWORD_URL = "api/change-password";
    public static final String UPLOAD_PROFILE_IMAGE_URL = "api/upload-avatar-picture";
    public static final String UPLOAD_MEDIA = "api/media";
    public static final String DELETE_MEDIA = "api/media/{uuid}";

}
