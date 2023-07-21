package io.innospots.connector.api.minder;

import io.innospots.base.data.enums.ApiMethod;
import io.innospots.base.data.http.HttpConnection;
import io.innospots.base.data.http.HttpData;
import io.innospots.base.json.JSONUtils;
import lombok.Getter;
import lombok.Setter;
import org.noear.snack.ONode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Smars
 * @date 2023/7/20
 */
@Getter
@Setter
public class TokenHolder {


    private ApiMethod apiMethod;

    private String tokenParam;

    private Integer cacheTime;

    private String jsonPath;

    private LocalDateTime expireTime;

    private TokenLocation tokenLoc;

    private String address;

    private String token;

    private String queryParam;
    private String postBody;

    private HttpData response;

    private HttpConnection httpConnection = new HttpConnection();

    public TokenHolder() {

    }

    public TokenHolder(ApiMethod apiMethod, String tokenParam, Integer cacheTime, String jsonPath, TokenLocation tokenLoc, String address, String token, String queryParam) {
        this.apiMethod = apiMethod;
        this.tokenParam = tokenParam;
        this.cacheTime = cacheTime;
        this.jsonPath = jsonPath;
        this.tokenLoc = tokenLoc;
        this.address = address;
        this.token = token;
        this.queryParam = queryParam;
    }

    public TokenHolder(ApiMethod apiMethod, String tokenParam, Integer cacheTime, String jsonPath, TokenLocation tokenLoc, String address, String token, String queryParam, String postBody) {
        this.apiMethod = apiMethod;
        this.tokenParam = tokenParam;
        this.cacheTime = cacheTime;
        this.jsonPath = jsonPath;
        this.tokenLoc = tokenLoc;
        this.address = address;
        this.token = token;
        this.queryParam = queryParam;
        this.postBody = postBody;
    }

    public String fetchToken(boolean cache) {

        LocalDateTime now = LocalDateTime.now();
        if (cache && expireTime != null && expireTime.isAfter(now) && token != null) {
            return token;
        }
        Map<String, Object> params = new HashMap<>();
        if (this.queryParam != null) {
            String[] ps = this.queryParam.split("&");
            for (String p : ps) {
                String[] ss = p.split("=");
                params.put(ss[0], ss[1]);
            }
        }

        if (apiMethod == ApiMethod.GET) {
            response = httpConnection.get(address, params, null);
            token = extractToken(response);
        } else if (apiMethod == ApiMethod.POST) {
            response = httpConnection.post(address, params, this.postBody, null);
            token = extractToken(response);
        }

        if (token != null) {
            expireTime = LocalDateTime.now().plusSeconds(cacheTime);
        }

        return token;
    }

    private String extractToken(HttpData httpData) {
        String t = null;
        if (httpData.getBody() instanceof Map) {
            ONode jsonNode = ONode.load(httpData.getBody());
            t = jsonNode.select(this.jsonPath).toObject();
            Object ct = jsonNode.select("$.expires_in");
            if(ct != null){
                this.cacheTime = Integer.parseInt(ct.toString());
            }else{
                ct = jsonNode.select("$.expires");
                if(ct!=null){
                    this.cacheTime = Integer.parseInt(ct.toString());
                }
            }
        } else if (httpData.getBody() instanceof String) {
            ONode jsonNode = ONode.load(JSONUtils.toMap((String) httpData.getBody()));
            t = jsonNode.select(this.jsonPath).toObject();
            Object ct = jsonNode.select("$.expires_in");
            if(ct != null){
                this.cacheTime = Integer.parseInt(ct.toString());
            }else{
                ct = jsonNode.select("$.expires");
                if(ct!=null){
                    this.cacheTime = Integer.parseInt(ct.toString());
                }
            }
        }
        return t;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("address='").append(address).append('\'');
        sb.append(", apiMethod=").append(apiMethod);
        sb.append(", queryParam='").append(queryParam).append('\'');
        sb.append(", tokenParam='").append(tokenParam).append('\'');
        sb.append(", cacheTime=").append(cacheTime);
        sb.append(", jsonPath='").append(jsonPath).append('\'');
        sb.append(", expireTime=").append(expireTime);
        sb.append(", tokenLoc=").append(tokenLoc);
        sb.append(", token='").append(token).append('\'');
        sb.append(", postBody='").append(postBody).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public enum TokenLocation {
        HEADER,
        BODY,
        PARAM;
    }
}

