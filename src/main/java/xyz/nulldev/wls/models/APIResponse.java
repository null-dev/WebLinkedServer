package xyz.nulldev.wls.models;

import xyz.nulldev.wls.utils.GSONUtils;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class APIResponse {

    private final Type responseType;
    private final String responseContent;
    private final String errorDesc;
    private final int responseCode;

    public static APIResponse newErrorResponse(String errorDesc, int code) {
        return new APIResponse(code, errorDesc, null, Type.ERROR);
    }

    public static APIResponse newSuccessResponse(String responseContent, int code) {
        return new APIResponse(code, null, responseContent, Type.SUCCESS);
    }

    public String asJSON() {
        return GSONUtils.getGson().toJson(this, APIResponse.class);
    }

    public APIResponse(int responseCode, String errorDesc, String responseContent, Type responseType) {
        this.responseCode = responseCode;
        this.errorDesc = errorDesc;
        this.responseContent = responseContent;
        this.responseType = responseType;
    }

    public Type getResponseType() {
        return responseType;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public enum Type {
        ERROR, SUCCESS
    }
}
