package io.innospots.connector.schema.service;

import io.innospots.base.data.schema.AppCredentialInfo;
import io.innospots.base.data.schema.ConnectionCredential;
import io.innospots.connector.schema.operator.AppCredentialOperator;
import io.innospots.connector.schema.reader.ConnectionCredentialReader;

/**
 * @author Smars
 * @date 2023/7/20
 */
public class Oauth2CallbackService {

    private ConnectionCredentialReader connectionCredentialReader;
    private AppCredentialOperator appCredentialOperator;

    public void authCallback(String appCode,String code,String state){
        AppCredentialInfo appCredentialInfo = new AppCredentialInfo();
        appCredentialInfo.setConfigCode("oauth2-auth-api");
        appCredentialInfo.setConnectorName("Http");
        appCredentialInfo.setAppNodeCode(appCode);
        ConnectionCredential connectionCredential = connectionCredentialReader.fillCredential(appCredentialInfo);

    }

}
