package common;

public class ResponseStat {
    public static Response responseClass;

    public static Response getExchangeClass() {
        return responseClass;
    }

    public static void setExchangeClass(Response responseClass) {
        ResponseStat.responseClass = responseClass;
    }
}
