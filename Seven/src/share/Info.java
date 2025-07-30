package share;

import java.io.Serializable;

/**
 * TODO 传输协议类
 *
 * @author Haroxa
 * @date 2023/12/23
 */
public class Info implements Serializable {
    private String msg;
    private Object data;

    public Info(String msg) {
        setMsg(msg);
    }

    public Info(String msg, Object data) {
        setMsg(msg);
        setData(data);
    }

    public static void main(String[] args) {

    }

    @Override
    public String toString() {
        return "Info{" +
                "msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
