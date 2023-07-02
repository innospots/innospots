From innospots-base:latest

ENV APPLICATION_NAME innospots-data-server

ENV SERVER_MAIN_CLASS io.innospots.data.server.InnospotDataServerApplication

ADD target/$APPLICATION_NAME.tar.gz /innospot/

ENTRYPOINT ["sh","/innospot/bin/app_entrypoint.sh"]