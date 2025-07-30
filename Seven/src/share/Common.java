package share;

/**
 * TODO 常量类
 *
 * @author Haroxa
 * @date 2023/12/23
 */
public enum Common {
    // 角色的枚举值
    VERIFY_USER("verify_user"),
    SEARCH_USER("search_user"),
    INSERT_USER("insert_user"),
    UPDATE_USER("update_user"),
    DELETE_USER("delete_user"),
    LIST_USER("list_user"),
    SEARCH_DOC("search_doc"),
    INSERT_DOC("insert_doc"),
    DOWNLOAD_DOC("download_doc"),
    UPLOAD_DOC("upload_doc"),
    DELETE_DOC("delete_doc"),
    LIST_DOC("list_doc");

    private final String name;
    public static final int MAX_BYTES = 1024, SERVER_PORT = 12345;
    public static final String SERVER_HOST = "127.0.0.1";
    public static final byte[] EOF_BYTES = "EOF!@#$%^&*()EOF".getBytes();
    Common(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
